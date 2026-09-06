package com.tfg.cultura.api.users.jwt;

import com.fasterxml.jackson.core.exc.StreamWriteException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.cultura.api.core.exception.ApiError;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import java.util.Set;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private static final org.slf4j.Logger log = LoggerFactory.getLogger("usersLogger");

	private final JwtService jwtService;
	private final CustomUserDetailsService userDetailsService;
	private final ObjectMapper mapper = new ObjectMapper();

	public JwtFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
		this.jwtService = jwtService;
		this.userDetailsService = userDetailsService;
	}

private static final Set<HttpMethod> ALL_HTTP_METHODS = Set.of(
		HttpMethod.GET,
		HttpMethod.POST,
		HttpMethod.PUT,
		HttpMethod.DELETE,
		HttpMethod.PATCH,
		HttpMethod.HEAD,
		HttpMethod.OPTIONS,
		HttpMethod.TRACE
);

private static final Map<String, Set<HttpMethod>> PUBLIC_PATHS = Map.ofEntries(
		Map.entry("/", ALL_HTTP_METHODS),
		Map.entry("/api", ALL_HTTP_METHODS),
		Map.entry("/api/", ALL_HTTP_METHODS),

		// Dummy
		Map.entry("/api/dummy", Set.of(HttpMethod.GET)),
		Map.entry("/api/dummy/**", Set.of(HttpMethod.GET)),

		// Swagger and API docs
		Map.entry("/v3/api-docs/**", Set.of(HttpMethod.GET)),
		Map.entry("/swagger-ui/**", Set.of(HttpMethod.GET)),
		Map.entry("/swagger-ui.html", Set.of(HttpMethod.GET)),
		Map.entry("/api/docs", Set.of(HttpMethod.GET)),
		Map.entry("/docs/**", Set.of(HttpMethod.GET)),
		Map.entry("/docs", Set.of(HttpMethod.GET)),
		Map.entry("/api/docs/**", Set.of(HttpMethod.GET)),
		Map.entry("/api/swagger-ui/**", Set.of(HttpMethod.GET)),

		// Users - Auth
		Map.entry("/api/users/auth/**", Set.of(HttpMethod.POST)),

		// Suggestions
		Map.entry("/api/suggestions", Set.of(HttpMethod.GET)),

		// Sections
		Map.entry("/api/sections", Set.of(HttpMethod.GET)),
		Map.entry("/api/sections/**", Set.of(HttpMethod.GET)),

		// Catalog
		Map.entry("/api/catalog", Set.of(HttpMethod.GET)),
		Map.entry("/api/catalog/**", Set.of(HttpMethod.GET))
);

private static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

private boolean isPublicPath(HttpServletRequest request) {

	String requestPath = request.getRequestURI();
	HttpMethod requestMethod = HttpMethod.valueOf(request.getMethod());

	return PUBLIC_PATHS.entrySet().stream()
			.anyMatch(entry ->
					PATH_MATCHER.match(entry.getKey(), requestPath)
							&& entry.getValue().contains(requestMethod));
}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		if (isPublicPath(request)) {
			log.debug("[JWT] Acceso a ruta pública {} sin autenticación", request.getRequestURI());
			filterChain.doFilter(request, response);
			return;
		}

		String authHeader = request.getHeader("Authorization");
		String path = request.getRequestURI();

		// No hay token: continuar sin autenticación
		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.debug("[JWT] Acceso a {} sin token", path);
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7);

		try {
			// Si ya existe una autenticación, no hacemos nada
			if (SecurityContextHolder.getContext().getAuthentication() != null) {
				filterChain.doFilter(request, response);
				return;
			}

			String userId = jwtService.extractId(token);

			if (userId == null) {
				log.debug("[JWT] No se pudo extraer el usuario del token para {}", path);
				filterChain.doFilter(request, response);
				return;
			}

			UserDetails userDetails = userDetailsService.loadUserById(userId);

			// El usuario existe pero está desactivado
			if (!userDetails.isEnabled()) {
				writeDisabledUserResponse(userId, response);
				return;
			}

			// Validar token
			if (jwtService.isTokenValid(token, userDetails)) {
				authenticateUser(userDetails, path, request);
			} else {
				log.debug("[JWT] Token inválido o expirado - Path: {}", path);
			}

		} catch (Exception e) {
			logCatchedException(path, e);
		}

		// SIEMPRE continuar si no hemos generado una respuesta propia
		filterChain.doFilter(request, response);
	}

	private void writeDisabledUserResponse(String userId, HttpServletResponse response)
			throws IOException, StreamWriteException, DatabindException {
		ApiError error = ApiError.builder().status(HttpStatus.FORBIDDEN.value()).message("Usuario desactivado").build();

		log.warn("[JWT] El usuario con id {} está desactivado", userId);

		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);

		mapper.writeValue(response.getWriter(), error);
	}

	private void logCatchedException(String path, Exception e) {
		switch (e.getClass().getSimpleName()) {
			case "ExpiredJwtException" :
				log.info("[JWT] Token expirado para path: {}", path);
				break;
			case "JwtException" :
				log.info("[JWT] Token inválido para path {}: {}", path, e.getMessage());
				break;
			case "UserNotFoundException" :
				log.info("[JWT] Usuario no encontrado para path {}: {}", path, e.getMessage());
				break;
			default :
				log.error("[JWT] Error inesperado para path {}: {}", path, e.getMessage());
		}
	}

	private void authenticateUser(UserDetails userDetails, String path, HttpServletRequest request) {
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(userDetails, null,
				userDetails.getAuthorities());

		authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

		SecurityContextHolder.getContext().setAuthentication(authToken);

		log.info("[JWT] Token válido para usuario: {} - Path: {}", userDetails.getUsername(), path);
	}
}
