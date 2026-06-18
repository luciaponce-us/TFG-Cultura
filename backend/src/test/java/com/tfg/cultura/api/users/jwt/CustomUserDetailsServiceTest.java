package com.tfg.cultura.api.users.jwt;

import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsService service;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserFactory.validUser();
        SecurityContextHolder.clearContext();
    }

    // -------------------------------
    // loadUserByUsername
    // -------------------------------

    @Test
    void should_load_user_by_username() {
        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));

        var result = service.loadUserByUsername(user.getUsername());

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());

        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    void should_throw_exception_when_user_not_found() {
        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.empty());

        String username = user.getUsername();

        assertThrows(UserNotFoundException.class, () ->
                service.loadUserByUsername(username));

        verify(userRepository).findByUsername(user.getUsername());
    }

    // -------------------------------
    // loadUserByUserId
    // -------------------------------
	@Test
    void should_load_user_by_id_successfully() throws Exception {
        // Arrange
        String userId = user.getId();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        // Act
        UserDetails result = service.loadUserById(userId);

        // Assert
        assertNotNull(result);
        assertTrue(result instanceof CustomUserDetails);

        CustomUserDetails custom = (CustomUserDetails) result;
        assertEquals(user.getId(), custom.getId());
        assertEquals(user.getUsername(), custom.getUsername());

        verify(userRepository).findById(userId);
    }

    @Test
    void should_throw_user_not_found_exception_when_user_does_not_exist() {
        // Arrange
        String userId = "non-existent-id";
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        // Act & Assert
        UserNotFoundException exception = assertThrows(
                UserNotFoundException.class,
                () -> service.loadUserById(userId)
        );

        assertEquals("El usuario con id " + userId + " no existe", exception.getMessage());

        verify(userRepository).findById(userId);
    }

    // -------------------------------
    // getCurrentUserDetails
    // -------------------------------

    @Test
    void should_return_current_user_details() {
        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(userRepository.existsById(user.getId()))
                .thenReturn(true);

        var auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        CustomUserDetails result = service.getCurrentUserDetails();

        assertNotNull(result);
        assertEquals(user.getId(), result.getId());

        verify(userRepository).existsById(user.getId());
    }

    @Test
    void should_throw_when_no_authentication() {
        SecurityContextHolder.clearContext();

        assertThrows(UnathenticatedException.class, () ->
                service.getCurrentUserDetails());
    }

    @Test
    void should_throw_when_not_authenticated() {
        var auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(UnathenticatedException.class, () ->
                service.getCurrentUserDetails());
    }

    @Test
    void should_throw_when_user_does_not_exist_in_database() {
        CustomUserDetails userDetails = new CustomUserDetails(user);

        when(userRepository.existsById(user.getId()))
                .thenReturn(false);

        var auth = new UsernamePasswordAuthenticationToken(
                userDetails, null, userDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(UserNotFoundException.class, () ->
                service.getCurrentUserDetails());

        verify(userRepository).existsById(user.getId());
    }

    @Test
    void should_throw_when_principal_is_not_custom_user_details() {
        Authentication auth = mock(Authentication.class);

        when(auth.isAuthenticated()).thenReturn(true);

        // Principal inválido (NO CustomUserDetails)
        when(auth.getPrincipal()).thenReturn("invalid-principal");

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(UnathenticatedException.class, () ->
                service.getCurrentUserDetails());
    }
}