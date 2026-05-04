package com.tfg.cultura.api.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.tfg.cultura.api.suggestions.repository.SuggestionRepository;
import com.tfg.cultura.api.users.exception.UserAlreadyExistsException;
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.model.dto.UserUpdateRequest;
import com.tfg.cultura.api.users.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private SuggestionRepository suggestionRepository;

    @InjectMocks
    private UserService service;

    private User user;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        user = UserFactory.validUser();
        updateRequest = UserFactory.validUserUpdateRequest();
    }

    // GET USER

    @Test
    void should_return_user_response_when_get_existing_user() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserResponse response = service.getUser(user.getUsername());

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
    }

    @Test
    void should_throw_exception_when_get_unexisting_user() {
        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.getUser("123"));

        assertTrue(ex.getMessage().contains("no existe"));
    }

    // FIND USER BY ID

    @Test
    void should_return_user_when_find_user_by_id_with_existing_user() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        User foundUser = service.findUserById(user.getId());

        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
    }

    @Test
    void should_throw_UserNotFoundException_when_find_user_by_id_with_unexisting_user() {
        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.findUserById("123"));

        assertTrue(ex.getMessage().contains("no existe"));
    }

    // UPDATE USER

    @Test
    void should_update_user_successfully() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponse response = service.updateUser(user.getUsername(), updateRequest);

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(updateRequest.getName(), response.getName());
        assertEquals(updateRequest.getSurname(), response.getSurname());
    }

    @Test
    void should_update_user_username_successfully() {
        String newUsername = "newUsername";
        updateRequest.setUsername(newUsername);

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(newUsername)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = service.updateUser(user.getUsername(), updateRequest);

        assertNotNull(response);
        assertEquals(newUsername, response.getUsername());
    }

    @Test
    void should_update_user_password_succesfully() {
        String username = user.getUsername();
        String oldEmail = user.getEmail();

        UserUpdateRequest request = new UserUpdateRequest();
        request.setPassword("newPassword");

        when(userRepository.findByUsername(username))
                .thenReturn(Optional.of(user));

        when(passwordEncoder.encode(any()))
                .thenReturn("encodedNewPassword");

        when(userRepository.save(any(User.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // WHEN
        UserResponse response = service.updateUser(username, request);

        // THEN
        assertEquals("encodedNewPassword", user.getPassword());
        assertEquals(oldEmail, response.getEmail()); // no cambia

        verify(passwordEncoder).encode("newPassword");
        verify(userRepository).save(user);

    }

    @Test
    void should_throw_UserNotFoundException_when_update_unexisting_user() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.updateUser("123", updateRequest));

        assertTrue(ex.getMessage().contains("no existe"));
    }

    @Test
    void should_throw_UserAlreadyExistsException_when_update_user_with_existing_username() {
        String username = user.getUsername();
        String existingUsername = "existingUsername";
        updateRequest.setUsername(existingUsername);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(existingUsername)).thenReturn(true);

        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
                () -> service.updateUser(username, updateRequest));

        assertTrue(ex.getMessage().contains("ya está en uso"));
    }

    @Test
    void should_throw_UserAlreadyExistsException_when_update_user_with_existing_dni() {
        String username = user.getUsername();
        String existingDni = "06323988T";
        updateRequest.setDni(existingDni);

        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));
        when(userRepository.existsByDni(existingDni)).thenReturn(true);
        UserAlreadyExistsException ex = assertThrows(UserAlreadyExistsException.class,
                () -> service.updateUser(username, updateRequest));

        assertTrue(ex.getMessage().contains("ya está en uso"));
    }

    // DELETE USER

    @Test
    void should_delete_user_successfully() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        service.deleteUser(user.getUsername());

        verify(userRepository).delete(user);
    }

    @Test
    void should_throw_UserNotFoundException_when_delete_unexisting_user() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.deleteUser("123"));

        assertTrue(ex.getMessage().contains("no existe"));
    }



}
