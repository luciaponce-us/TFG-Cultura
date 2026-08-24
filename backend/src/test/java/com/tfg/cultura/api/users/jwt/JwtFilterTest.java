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
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.io.PrintWriter;
import java.io.StringWriter;

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
    // doFilterInternal
    // -------------------------------

    @Test
    void should_continue_when_no_authorization_header() throws Exception {
        when(request.getHeader("Authorization")).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void should_continue_when_header_does_not_start_with_bearer() throws Exception {
        when(request.getHeader("Authorization")).thenReturn("Basic 123");

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verifyNoInteractions(jwtService, userDetailsService);
    }

    @Test
    void should_authenticate_when_token_is_valid() throws Exception {
        String token = "validToken";
        String username = "lucia";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractId(token)).thenReturn(username);
        when(userDetailsService.loadUserById(username)).thenReturn(userDetails);
        when(userDetails.isEnabled()).thenReturn(true);
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
    void should_not_authenticate_when_token_is_invalid() throws Exception {
        String token = "invalidToken";
        String username = "lucia";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractId(token)).thenReturn(username);
        when(userDetailsService.loadUserById(username)).thenReturn(userDetails);
        when(userDetails.isEnabled()).thenReturn(true);
        when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);

        // No autenticado
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }

    @Test
    void should_not_authenticate_when_username_is_null() throws Exception {
        String token = "token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractId(token)).thenReturn(null);

        filter.doFilterInternal(request, response, filterChain);

        verify(filterChain).doFilter(request, response);
        verify(userDetailsService, never()).loadUserById(any());
    }

    @Test
    void should_skip_when_already_authenticated() throws Exception {
        String token = "token";

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        
        // Simular autenticación previa
        SecurityContextHolder.getContext().setAuthentication(mock(Authentication.class));

        filter.doFilterInternal(request, response, filterChain);

        verify(userDetailsService, never()).loadUserById(any());
        verify(filterChain).doFilter(request, response);
    }

    @Test
    void should_return_403_when_user_is_disabled() throws Exception {
        String token = "validToken";
        String userId = "lucia";

        StringWriter stringWriter = new StringWriter();
        PrintWriter writer = new PrintWriter(stringWriter);

        when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
        when(jwtService.extractId(token)).thenReturn(userId);
        when(userDetailsService.loadUserById(userId)).thenReturn(userDetails);
        when(userDetails.isEnabled()).thenReturn(false);
        when(response.getWriter()).thenReturn(writer);

        filter.doFilterInternal(request, response, filterChain);

        verify(response).setStatus(HttpServletResponse.SC_FORBIDDEN);
        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);

        verify(jwtService, never()).isTokenValid(any(), any());
        verify(filterChain, never()).doFilter(any(), any());

        writer.flush();

        String json = stringWriter.toString();
        assertTrue(json.contains("\"status\":403"));
        assertTrue(json.contains("Usuario desactivado"));
        assertTrue(json.contains("Tu usuario está desactivado"));
    }
}