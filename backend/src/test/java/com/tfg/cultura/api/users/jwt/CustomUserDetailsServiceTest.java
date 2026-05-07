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
    void shouldLoadUserByUsername() {
        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.of(user));

        var result = service.loadUserByUsername(user.getUsername());

        assertNotNull(result);
        assertEquals(user.getUsername(), result.getUsername());

        verify(userRepository).findByUsername(user.getUsername());
    }

    @Test
    void shouldThrowExceptionWhenUserNotFound() {
        when(userRepository.findByUsername(user.getUsername()))
                .thenReturn(Optional.empty());

        String username = user.getUsername();

        assertThrows(UserNotFoundException.class, () ->
                service.loadUserByUsername(username));

        verify(userRepository).findByUsername(user.getUsername());
    }

    // -------------------------------
    // getCurrentUserDetails
    // -------------------------------

    @Test
    void shouldReturnCurrentUserDetails() {
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
    void shouldThrowWhenNoAuthentication() {
        SecurityContextHolder.clearContext();

        assertThrows(UnathenticatedException.class, () ->
                service.getCurrentUserDetails());
    }

    @Test
    void shouldThrowWhenNotAuthenticated() {
        var auth = mock(org.springframework.security.core.Authentication.class);
        when(auth.isAuthenticated()).thenReturn(false);

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(UnathenticatedException.class, () ->
                service.getCurrentUserDetails());
    }

    @Test
    void shouldThrowWhenUserDoesNotExistInDatabase() {
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
    void shouldThrowWhenPrincipalIsNotCustomUserDetails() {
        Authentication auth = mock(Authentication.class);

        when(auth.isAuthenticated()).thenReturn(true);

        // Principal inválido (NO CustomUserDetails)
        when(auth.getPrincipal()).thenReturn("invalid-principal");

        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThrows(UnathenticatedException.class, () ->
                service.getCurrentUserDetails());
    }
}