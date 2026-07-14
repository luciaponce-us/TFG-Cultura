package com.tfg.cultura.api.users.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tfg.cultura.api.core.exception.ApiError;
import com.tfg.cultura.api.users.exception.UserNotFoundException;

import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger("usersLogger");

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;
    private final ObjectMapper mapper = new ObjectMapper();


    // URLs públicas que no requieren autenticación
    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/api/users/auth/register",
            "/api/users/auth/login",
            "/v3/api-docs",
            "/swagger-ui",
            "/swagger-ui.html",
            "/api/docs",
            "/docs",
            "/api/dummy");

    public JwtFilter(JwtService jwtService, CustomUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        boolean isPublic = PUBLIC_URLS.stream().anyMatch(path::startsWith);
        log.debug("[JWT] Accediendo a \"{}\" (ruta {})", path, isPublic? "pública" : "protegida");
        return isPublic;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException, UserNotFoundException {
                
        String authHeader = request.getHeader("Authorization");
        String path = request.getRequestURI();

        // Sin token
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.warn("[JWT] Se ha intentado acceder a {} sin token", path);
            filterChain.doFilter(request, response);
            return;
        }

        // Extraer token
        String token = authHeader.substring(7);

        try {
            // Extraer id del usuario
            String userId = jwtService.extractId(token);

            // Si hay id y no hay autenticación previa
            if (userId != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserById(userId);
                log.info("[JWT] El usuario con id {} ha intentado acceder a {}", userId, path);

                // Devolver 403 si el usuario no está activado
                if (!userDetails.isEnabled()) {
                    ApiError error = ApiError.builder()
                            .status(HttpStatus.FORBIDDEN.value())
                            .error("Usuario desactivado")
                            .message("Tu usuario está desactivado")
                            .build();
                    log.warn("[JWT] El usuario con id {} está desactivado", userId);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);

                    mapper.writeValue(response.getWriter(), error);
                    return;
                }

                // Validar token
                if (jwtService.isTokenValid(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("[JWT] Token válido para usuario: {} - Path: {}", userDetails.getUsername(), path);
                } else {
                    log.warn("[JWT] Token inválido o expirado para usuario: {} - Path: {}", userDetails.getUsername(), path);
                }
            } else if (userId != null) {
                log.debug("[JWT] Usuario con id {} ya tiene autenticación previa", userId);
            }
        } catch (io.jsonwebtoken.JwtException e) {
            log.error("[JWT] Error procesando JWT para path {}: {}", path, e.getMessage(), e);
        } catch (UserNotFoundException e) {
            log.error("[JWT] Usuario no encontrado en JWT para path {}: {}", path, e.getMessage());
        } catch (Exception e) {
            log.error("[JWT] Error inesperado en JwtFilter para path {}: {}", path, e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}
