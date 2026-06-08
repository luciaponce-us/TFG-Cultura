package com.tfg.cultura.api.users.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.Authentication;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class JwtFilterTest {

    @Mock
    private JwtService jwtService;

    @Mock
    private CustomUserDetailsService userDetailsService;

    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private FilterChain filterChain;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private JwtFilter filter;

    @BeforeEach
    void setUp() {
        SecurityContextHolder.clearContext();
    }

    // -------------------------------
    // shouldNotFilter
    // -------------------------------

    @Test
    void shouldNotFilterPublicUrls() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/users/auth/register");

        boolean result = filter.shouldNotFilter(request);

        assertTrue(result);
    }

    @Test
    void shouldFilterNonPublicUrls() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/private");

        boolean result = filter.shouldNotFilter(request);

        assertFalse(result);
    }

    // -------------------------------
    // doFilterInternal
    // -------------------------------

    @Test
    void shouldContinueWhenNoAuthorizationHeader() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void shouldContinueWhenHeaderDoesNotStartWithBearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic 123");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void shouldAuthenticateWhenTokenIsValid() throws Exception {
        String token = "validToken";
        String username = "lucia";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractId(token)).thenReturn(username);
        when(userDetailsService.loadUserById(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);
        when(userDetails.getAuthorities()).thenReturn(java.util.List.of());

        filter.doFilterInternal(request, response, filterChain);

        verify(jwtService).extractId(token);
        verify(userDetailsService).loadUserById(username);
        verify(jwtService).isTokenValid(token, userDetails);

        verify(filterChain).doFilter(request, response);

        // Verifica que se ha autenticado
        assertNotNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldNotAuthenticateWhenTokenInvalid() throws Exception {
        String token = "invalidToken";
        String username = "lucia";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractId(token)).thenReturn(username);
        when(userDetailsService.loadUserById(username)).thenReturn(userDetails);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        // No autenticado
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void shouldNotAuthenticateWhenUsernameIsNull() throws Exception {
        String token = "token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractId(token)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(userDetailsService, never()).loadUserById(any());
    }

    @Test
    void shouldSkipWhenAlreadyAuthenticated() throws Exception {
        String token = "token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractId(token)).thenReturn("lucia");

        // Simular autenticación previa
        SecurityContextHolder.getContext().setAuthentication(mock(Authentication.class));

        filter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService, never()).loadUserById(any());
        verify(filterChain).doFilter(request, response);
    }
}