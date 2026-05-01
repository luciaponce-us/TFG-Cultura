package com.tfg.cultura.api.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService service;

    private User user;

    @BeforeEach
    void setUp() {
        user = UserFactory.validUser();
    }

    // GET USER

    @Test
    void should_return_user_response_when_get_existing_user() {
        when(userRepository.findById(anyString())).thenReturn(Optional.of(user));

        UserResponse response = service.getUserById("123");

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
    }

    @Test
    void should_throw_exception_when_get_unexisting_user() {
        when(userRepository.findById(any())).thenReturn(Optional.empty());

        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.getUserById("123"));

        assertTrue(ex.getMessage().contains("no existe"));
    }

}
