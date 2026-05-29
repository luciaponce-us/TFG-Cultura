package com.tfg.cultura.api.users.jwt;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.tfg.cultura.api.users.exception.UserNotFoundException;

import org.slf4j.LoggerFactory;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(JwtFilter.class);

    private final JwtService jwtService;
    private final CustomUserDetailsService userDetailsService;

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
        log.info("JwtFilter check - Path: {}, IsPublic: {}", path, isPublic);
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
        log.debug("JwtFilter processing path: {}, AuthHeader: {}", path, authHeader != null ? "Present" : "Missing");

        // 1. Si no hay token, continuar
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            log.debug("No token found for path: {}", path);
            filterChain.doFilter(request, response);
            return;
        }

        // 2. Extraer token
        String token = authHeader.substring(7);

        try {
            // 3. Extraer username
            String username = jwtService.extractUsername(token);
            log.debug("Extracted username from token: {}", username);

            // 4. Si hay username y no hay autenticación previa
            if (username != null &&
                    SecurityContextHolder.getContext().getAuthentication() == null) {

                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                log.debug("Loaded user details for username: {}", username);

                // 5. Validar token
                if (jwtService.isTokenValid(token, userDetails)) {

                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities());

                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.info("Token válido para usuario: {} - Path: {}", username, path);
                } else {
                    log.warn("Token inválido o expirado para usuario: {} - Path: {}", username, path);
                }
            } else if (username != null) {
                log.debug("Usuario {} ya tiene autenticación previa", username);
            }
        } catch (io.jsonwebtoken.JwtException e) {
            log.error("Error procesando JWT para path {}: {}", path, e.getMessage(), e);
        } catch (UserNotFoundException e) {
            log.error("Usuario no encontrado en JWT para path {}: {}", path, e.getMessage());
        } catch (Exception e) {
            log.error("Error inesperado en JwtFilter para path {}: {}", path, e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }
}
