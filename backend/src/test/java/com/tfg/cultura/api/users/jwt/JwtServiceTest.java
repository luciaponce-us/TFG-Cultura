package com.tfg.cultura.api.users.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Field;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;

class JwtServiceTest {
    private JwtService jwtService;

    private static final String SECRET = "mySuperSecretKeyThatIsLongEnoughForHS256Algorithm12345";
    private static final long EXPIRATION = 1000 * 60 * 60; // 1 hora

    private CustomUserDetails userDetails;
    private String username;
    private Role role;
    private String id;

    @BeforeEach
    void setUp() throws Exception {
        jwtService = new JwtService();

        // Inyectar valores de @Value manualmente
        setField(jwtService, "secret", SECRET);
        setField(jwtService, "expiration", EXPIRATION);

        jwtService.init();

        User user = UserFactory.validUser();
        userDetails = new CustomUserDetails(user);
        username = user.getUsername();
        role = user.getRole();
        id = user.getId();
    }

    private void setField(Object target, String fieldName, Object value) throws Exception {
        Field field = JwtService.class.getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(target, value);
    }

    @Test
    void shouldGenerateTokenAndExtractAllFields() {
        String token = jwtService.generateToken(username, role, id);

        assertNotNull(token);
        assertEquals(username, jwtService.extractUsername(token));
        assertEquals(role, jwtService.extractRole(token));
        assertEquals(id, jwtService.extractId(token));
    }

    @Test
    void shouldReturnTrueWhenTokenIsValid() {
        String token = jwtService.generateToken(username, role, id);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void shouldReturnFalseWhenIdDoesNotMatch() {
        String token = jwtService.generateToken(username, role, "otroId");

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void shouldDetectExpiredToken() throws Exception {
        // Token con expiración muy corta
        setField(jwtService, "expiration", -1000L);
        jwtService.init();

        String token = jwtService.generateToken(username, role, id);

        assertTrue(jwtService.isTokenExpired(token));
    }

    @Test
    void shouldReturnFalseWhenTokenNotExpired() {
        String token = jwtService.generateToken(username, role, id);

        assertFalse(jwtService.isTokenExpired(token));
    }

}
