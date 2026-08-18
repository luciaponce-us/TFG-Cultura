package com.tfg.cultura.api.users.jwt;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.factory.AppPropertiesFactory;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.enumerators.Role;
import org.springframework.security.core.userdetails.UserDetails;

class JwtServiceTest {
    private JwtService jwtService;

    private static final String SECRET = "mySuperSecretKeyThatIsLongEnoughForHS256Algorithm12345";

    private CustomUserDetails userDetails;
    private String username;
    private Role role;
    private String id;

    @BeforeEach
    void setUp() {
        AppProperties appProperties = AppPropertiesFactory.validAppProperties();

        jwtService = new JwtService(appProperties);
        jwtService.init();

        User user = UserFactory.validUser();
        userDetails = new CustomUserDetails(user);
        username = user.getUsername();
        role = user.getRole();
        id = user.getId();
    }

    @Test
    void should_generate_token_and_extract_all_fields() {
        String token = jwtService.generateToken(username, role, id);

        assertNotNull(token);
        assertEquals(username, jwtService.extractUsername(token));
        assertEquals(role, jwtService.extractRole(token));
        assertEquals(id, jwtService.extractId(token));
    }

    @Test
    void should_return_true_when_token_is_valid() {
        String token = jwtService.generateToken(username, role, id);

        assertTrue(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void should_return_false_when_id_does_not_match() {
        String token = jwtService.generateToken(username, role, "otroId");

        assertFalse(jwtService.isTokenValid(token, userDetails));
    }

    @Test
    void should_detect_expired_token() {
        AppProperties appPropertiesExpiredToken = AppPropertiesFactory.validAppPropertiesWithJwt(SECRET, -1000L);

        JwtService jwtServiceExpired = new JwtService(appPropertiesExpiredToken);
        jwtServiceExpired.init();

        String token = jwtServiceExpired.generateToken(username, role, id);

        assertTrue(jwtServiceExpired.isTokenExpired(token));
    }

    @Test
    void should_return_false_when_token_not_expired() {
        String token = jwtService.generateToken(username, role, id);

        assertFalse(jwtService.isTokenExpired(token));
    }

    @Test
    void should_return_false_when_user_details_not_custom() {
        String token = jwtService.generateToken(username, role, id);
        UserDetails loggedUser = null;

        assertFalse(jwtService.isTokenValid(token, loggedUser));
    }

}
