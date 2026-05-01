package com.tfg.cultura.api.users.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;

import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.users.exception.SelfActivationNotAllowedException;
import com.tfg.cultura.api.users.exception.UserAlreadyExistsException;
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.jwt.JwtService;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.dto.UserLoginRequest;
import com.tfg.cultura.api.users.model.dto.UserRegisterRequest;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.repository.UserRepository;

import static org.mockito.ArgumentMatchers.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private UserFileService userFileService;

    @InjectMocks
    private UserService userService;

    private UserRegisterRequest register = new UserRegisterRequest();
    private User user = new User();
    private UserLoginRequest loginRequest = new UserLoginRequest();
    private static final MockMultipartFile PDF_FILE = UserFactory.valid_payment_receipt_file();
    private static final MockMultipartFile AVATAR_FILE = UserFactory.valid_avatar_file();

    @BeforeEach
    void setUp() {
        register = UserFactory.validUserRegisterRequest();
        user = UserFactory.validUser();
        loginRequest = UserFactory.loginRequest();
        
    }

    private void mockUserRegistration() {
        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(passwordEncoder.encode(any())).thenReturn("encodedPassword");
    }

    @Test
    void should_return_user_response_when_registering_user() {
        mockUserRegistration();

        UserResponse response = userService.register(register, null, PDF_FILE);

        assertNotNull(response, "No se ha registrado el usuario");
        assertEquals(register.getUsername(), response.getUsername());
    }

    @Test
    void should_set_avatar_placeholder_when_registering_user_without_avatar() {
        mockUserRegistration();
        UserResponse response = userService.register(register, null, PDF_FILE);

        assertNotNull(response, "No se ha registrado el usuario");
        assertEquals(register.getUsername(), response.getUsername());
        assertEquals(UserFileService.AVATAR_PLACEHOLDER, response.getAvatar(), "No se ha asignado el avatar placeholder");
    }

    @Test
    void should_upload_avatar_when_registering_user_with_avatar() {
        mockUserRegistration();
        when(userFileService.uploadAvatar(anyString(), any())).thenReturn("url/avatar.png");

        UserResponse response = userService.register(register, AVATAR_FILE, PDF_FILE);

        assertNotNull(response, "No se ha registrado el usuario");
        assertEquals(register.getUsername(), response.getUsername());
        assertEquals("url/avatar.png", response.getAvatar());
    }

    @Test
    void should_throw_exception_when_registering_user_with_existing_username() {
        when(userRepository.existsByUsername(register.getUsername()))
                .thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
                () -> userService.register(register,null, PDF_FILE));
        assertTrue(ex.getMessage().contains("nombre de usuario"));
    }

    @Test
    void should_throw_exception_when_registering_user_with_existing_dni() {
        when(userRepository.existsByDni(register.getDni()))
                .thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
                () -> userService.register(register, null, PDF_FILE));
        assertTrue(ex.getMessage().contains("DNI"));
    }

    // LOGIN

    @Test
    void should_return_token_when_login_successfully() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(true);
        when(userDetailsService.loadUserByUsername(user.getUsername())).thenReturn(new CustomUserDetails(user));
        when(jwtService.generateToken(any(), any(), any())).thenReturn("test-token");

        String response = userService.login(loginRequest);

        assertNotNull(response);
        assertEquals("test-token", response);
    }

    @Test
    void should_throw_exception_when_login_with_unexisting_username() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> userService.login(loginRequest));

        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    void should_throw_exception_when_login_with_disabled_user() {
        user.setActive(false);
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));

        DisabledException ex = assertThrows(DisabledException.class, () -> userService.login(loginRequest));

        assertTrue(ex.getMessage().contains("desactivado"));
    }

    @Test
    void should_throw_exception_when_login_with_wrong_password() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(passwordEncoder.matches(any(), any())).thenReturn(false);

        BadCredentialsException ex = assertThrows(BadCredentialsException.class,
                () -> userService.login(loginRequest));

        assertTrue(ex.getMessage().contains("Credenciales inválidas"));
    }

    // ACTIVATE USER

    @Test
    void should_return_user_response_when_activate_successfully() {
        UserFactory.mockAuthContext();
        user.setActive(false);
        user.setId("123"); // Usuario distinto a sí mismo
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

        UserResponse response = userService.activateUser("123");

        assertNotNull(response);
        assertTrue(response.isActive());
    }

    @Test
    void should_return_user_response_when_activate_already_active_user() {
        UserFactory.mockAuthContext();
        user.setActive(true);
        user.setId("123"); // Usuario distinto a sí mismo
        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UserResponse response = userService.activateUser("123");
        assertNotNull(response);
        assertTrue(response.isActive());
    }

    @Test
    void should_throw_exception_when_activate_unexisting_user() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> userService.activateUser("123"));

        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    void should_throw_exception_when_user_activates_himself() {
        UserFactory.mockAuthContext();
        user.setActive(false);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        assertThrows(SelfActivationNotAllowedException.class, () -> {
            userService.activateUser("123");
        });

        SecurityContextHolder.clearContext();
    }

    @Test
    void should_throw_exception_when_activating_user_unathenticated() {
        SecurityContextHolder.clearContext();
        when(userRepository.findById(any())).thenReturn(Optional.of(user));
        UnathenticatedException ex = assertThrows(UnathenticatedException.class, () -> {
            userService.activateUser("123");
        });
        assertTrue(ex.getMessage().contains("autenticación"));
    }

    @Test
    void should_throw_exception_when_activating_user_and_no_user_details() {
        Authentication auth = mock(Authentication.class);
        when(auth.isAuthenticated()).thenReturn(true);

        SecurityContext context = mock(SecurityContext.class);
        when(context.getAuthentication()).thenReturn(auth);

        SecurityContextHolder.setContext(context);

        when(userRepository.findById(any())).thenReturn(Optional.of(user));

        UnathenticatedException ex = assertThrows(UnathenticatedException.class, () -> {
            userService.activateUser("123");
        });
        assertTrue(ex.getMessage().contains("información"));
        SecurityContextHolder.clearContext();
    }

    // GET USER

    @Test
    void should_return_user_response_when_get_existing_user() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        UserResponse response = userService.getUserById("123");

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
    }

    @Test
    void should_throw_exception_when_get_unexisting_user() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> userService.getUserById("123"));

        assertTrue(ex.getMessage().contains("no existe"));
    }

}
