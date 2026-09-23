package com.tfg.cultura.api.users.jwt;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;

import com.tfg.cultura.api.users.exception.UserNotFoundException;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;

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
		mockPrivatePath();
	}

	// -------------------------------
	// doFilterInternal
	// -------------------------------

	private void mockPrivatePath() {
		when(request.getRequestURI()).thenReturn("/api/private");
		when(request.getMethod()).thenReturn(HttpMethod.GET.name());
	}

	@Test
	void should_continue_without_authentication_when_path_is_public() throws Exception {
		when(request.getRequestURI()).thenReturn("/api/users/login");
		when(request.getMethod()).thenReturn(HttpMethod.POST.name());

		filter.doFilterInternal(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
		verifyNoInteractions(jwtService, userDetailsService);
	}

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
		String userId = "lucia";

		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
		when(jwtService.extractId(token)).thenReturn(userId);
		when(userDetailsService.loadUserById(userId)).thenReturn(userDetails);
		when(userDetails.isEnabled()).thenReturn(true);
		when(jwtService.isTokenValid(token, userDetails)).thenReturn(true);
		when(userDetails.getAuthorities()).thenReturn(java.util.List.of());

		filter.doFilterInternal(request, response, filterChain);

		verify(jwtService).extractId(token);
		verify(userDetailsService).loadUserById(userId);
		verify(jwtService).isTokenValid(token, userDetails);

		verify(filterChain).doFilter(request, response);

		assertNotNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void should_not_authenticate_when_token_is_invalid() throws Exception {
		String token = "invalidToken";
		String userId = "lucia";

		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
		when(jwtService.extractId(token)).thenReturn(userId);
		when(userDetailsService.loadUserById(userId)).thenReturn(userDetails);
		when(userDetails.isEnabled()).thenReturn(true);
		when(jwtService.isTokenValid(token, userDetails)).thenReturn(false);

		filter.doFilterInternal(request, response, filterChain);

		verify(jwtService).extractId(token);
		verify(userDetailsService).loadUserById(userId);
		verify(jwtService).isTokenValid(token, userDetails);

		verify(filterChain).doFilter(request, response);
		assertNull(SecurityContextHolder.getContext().getAuthentication());
	}

	@Test
	void should_continue_when_user_id_is_null() throws Exception {
		String token = "token";

		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
		when(jwtService.extractId(token)).thenReturn(null);

		filter.doFilterInternal(request, response, filterChain);

		verify(jwtService).extractId(token);
		verify(userDetailsService, never()).loadUserById(any());

		verify(filterChain).doFilter(request, response);
	}

	@Test
	void should_skip_when_already_authenticated() throws Exception {
		String token = "token";

		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);

		Authentication authentication = mock(Authentication.class);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		filter.doFilterInternal(request, response, filterChain);

		verify(userDetailsService, never()).loadUserById(any());
		verify(jwtService, never()).extractId(any());
		verify(jwtService, never()).isTokenValid(any(), any());

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
	}

	@Test
	void should_continue_when_token_extraction_throws_exception() throws Exception {
		String token = "token";

		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
		when(jwtService.extractId(token)).thenThrow(new RuntimeException("Invalid token"));

		filter.doFilterInternal(request, response, filterChain);

		verify(jwtService).extractId(token);
		verify(filterChain).doFilter(request, response);
	}

	@Test
	void should_continue_when_loading_user_throws_exception() throws Exception {
		String token = "token";
		String userId = "lucia";

		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
		when(jwtService.extractId(token)).thenReturn(userId);
		when(userDetailsService.loadUserById(userId))
				.thenThrow(new RuntimeException("User not found"));

		filter.doFilterInternal(request, response, filterChain);

		verify(jwtService).extractId(token);
		verify(userDetailsService).loadUserById(userId);
		verify(filterChain).doFilter(request, response);
	}

	// -------------------------------
	// logCatchedException
	// -------------------------------

	@Test
	void should_handle_expired_jwt_exception() throws Exception {
		String token = "token";

		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
		when(jwtService.extractId(token)).thenThrow(mock(ExpiredJwtException.class));

		filter.doFilterInternal(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
	}

	@Test
	void should_handle_jwt_exception() throws Exception {
		String token = "token";

		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
		when(jwtService.extractId(token)).thenThrow(mock(JwtException.class));

		filter.doFilterInternal(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
	}

	@Test
	void should_handle_user_not_found_exception() throws Exception {
		String token = "token";
		String userId = "lucia";

		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
		when(jwtService.extractId(token)).thenReturn(userId);
		when(userDetailsService.loadUserById(userId))
				.thenThrow(mock(UserNotFoundException.class));

		filter.doFilterInternal(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
	}

	@Test
	void should_handle_unexpected_exception() throws Exception {
		String token = "token";

		when(request.getHeader("Authorization")).thenReturn("Bearer " + token);
		when(jwtService.extractId(token))
				.thenThrow(new RuntimeException("Unexpected error"));

		filter.doFilterInternal(request, response, filterChain);

		verify(filterChain).doFilter(request, response);
	}
}