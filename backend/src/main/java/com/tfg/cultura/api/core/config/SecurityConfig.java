package com.tfg.cultura.api.core.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.tfg.cultura.api.users.jwt.JwtFilter;
import com.tfg.cultura.api.users.model.enumerators.Role;

import lombok.RequiredArgsConstructor;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final AppProperties appProperties;

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) {
        String[] adminRoles = appProperties.adminRoles().stream()
                .map(Role::name)
                .toArray(String[]::new);

        String[] superAdminRoles = List.of(Role.COORDINADOR.name(), Role.SECRETARIO.name()).toArray(new String[0]);

        http
                .cors(Customizer.withDefaults())
                .csrf(csrf -> csrf.disable()) // NOSONAR: CSRF deshabilitado porque la API es stateless y usa JWT en
                                              // headers (no cookies)
                .sessionManagement(sm -> sm.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        // Rutas públicas
                        .requestMatchers("/", "/api", "/api/").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/dummy", "/api/dummy/**").permitAll()
                        .requestMatchers(
                                // Swagger and API docs
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api/docs",
                                "/docs/**",
                                "/docs",
                                "/api/docs/**",
                                "/api/swagger-ui/**")
                        .permitAll()

                        // Users - Auth
                        .requestMatchers(HttpMethod.POST, "/api/users/auth/**").permitAll()
                        // Users - Profile (requiere autenticación)
                        .requestMatchers("/api/users/profile", "/api/users/profile/**").authenticated()
                        // Users - Admin (requiere roles específicos)
                        .requestMatchers("/api/users/*/toggle-activation").hasAnyRole(adminRoles)
                        .requestMatchers("/api/users", "/api/users/**").hasAnyRole(adminRoles)
                        // Suggestions
                        .requestMatchers(HttpMethod.GET, "/api/suggestions").permitAll()
                        // Sections
                        .requestMatchers(HttpMethod.GET, "/api/sections", "/api/sections/**").permitAll()
                        .requestMatchers("/api/sections", "/api/sections/**").hasAnyRole(superAdminRoles) // RN-11
                        // Catalog
                        .requestMatchers(HttpMethod.GET, "/api/catalog", "/api/catalog/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/catalog/**").hasAnyRole(adminRoles)
                        .requestMatchers(HttpMethod.PUT, "/api/catalog/**").hasAnyRole(adminRoles)
                        .requestMatchers(HttpMethod.DELETE, "/api/catalog/**").hasAnyRole(adminRoles)
                        .anyRequest().authenticated())
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(basic -> basic.disable())
                .formLogin(form -> form.disable());

        return http.build();
    }

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of(appProperties.frontendUrl()));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

}