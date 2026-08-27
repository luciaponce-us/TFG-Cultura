package com.tfg.cultura.api.users.jwt;

import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.users.model.enumerators.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JwtService {
	private final AppProperties appProperties;

	private SecretKey key;

	@PostConstruct
	public void init() {
		// Genera una clave segura a partir del secret
		this.key = Keys.hmacShaKeyFor(appProperties.jwt().secret().getBytes(StandardCharsets.UTF_8));
	}

	public String generateToken(String username, Role role, String id) {
		return Jwts.builder().subject(username).claim("role", role.name()).claim("id", id).issuedAt(new Date())
				.expiration(new Date(System.currentTimeMillis() + appProperties.jwt().expiration())).signWith(key)
				.compact();
	}

	public String extractUsername(String token) {
		return extractAllClaims(token).getSubject();
	}

	public Role extractRole(String token) {
		String role = extractAllClaims(token).get("role", String.class);
		return Role.valueOf(role);
	}

	public String extractId(String token) {
		return extractAllClaims(token).get("id", String.class);
	}

	private Claims extractAllClaims(String token) {
		return Jwts.parser().verifyWith(key).build().parseSignedClaims(token).getPayload();
	}

	public boolean isTokenExpired(String token) {
		try {
			return extractAllClaims(token).getExpiration().before(new Date());
		} catch (io.jsonwebtoken.ExpiredJwtException e) {
			return true;
		}
	}

	public boolean isTokenValid(String token, UserDetails userDetails) {
		if (!(userDetails instanceof CustomUserDetails customUserDetails)) {
			return false;
		}

		final String id = extractId(token);
		return id.equals(customUserDetails.getId()) && !isTokenExpired(token);
	}

}
