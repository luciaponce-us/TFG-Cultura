package com.tfg.cultura.api.users.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import com.tfg.cultura.api.config.MockConfig;
import com.tfg.cultura.api.users.model.User;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Import(MockConfig.class)
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;

    private static final String EXISTING_USERNAME = "lucia";
    private static final String EXISTING_DNI = "33419630D";
    private static final String UNEXISTING_USERNAME = "elena";
    private static final String UNEXISTING_DNI = "39350606K";

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();

        User userToSave = User.builder()
                .username(EXISTING_USERNAME)
                .password("12345678")
                .name("Lucía")
                .surname("García de Sola")
                .dni(EXISTING_DNI)
                .phone("600123123")
                .email("lucia@test.com")
                .build();

        userRepository.save(userToSave);
    }

    @Test
    void existsByUsername_should_return_true_if_exists() {
        boolean exists = userRepository.existsByUsername(EXISTING_USERNAME);
        assertTrue(exists);
    }

    @Test
    void existsByUsername_should_return_false_if_not_exists() {
        boolean exists = userRepository.existsByUsername(UNEXISTING_USERNAME);
        assertFalse(exists);
    }

    @Test
    void existsByDni_should_return_true_if_exists() {
        boolean exists = userRepository.existsByDni(EXISTING_DNI);
        assertTrue(exists);
    }

    @Test
    void existsByDni_should_return_false_if_not_exists() {
        boolean exists = userRepository.existsByDni(UNEXISTING_DNI);
        assertFalse(exists);
    }

    @Test
    void findByUsername_should_return_user_if_exists() {
        Optional<User> resultado = userRepository.findByUsername(EXISTING_USERNAME);

        assertTrue(resultado.isPresent());
        assertEquals(EXISTING_USERNAME, resultado.get().getUsername());
    }

    @Test
    void findByUsername_deberia_devolver_empty_si_no_existe() {
        Optional<User> resultado = userRepository.findByUsername(UNEXISTING_USERNAME);

        assertTrue(resultado.isEmpty());
    }

}