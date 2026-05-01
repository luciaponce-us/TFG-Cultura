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
import com.tfg.cultura.api.users.model.dto.UserUpdateRequest;
import com.tfg.cultura.api.users.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService service;

    private User user;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        user = UserFactory.validUser();
        updateRequest = UserFactory.validUserUpdateRequest();
    }

    // GET USER

    @Test
    void should_return_user_response_when_get_existing_user() {
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(user));

        UserResponse response = service.getUser(user.getUsername());

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
    }

    @Test
    void should_throw_exception_when_get_unexisting_user() {
        UserNotFoundException ex = assertThrows(UserNotFoundException.class,
                () -> service.getUser("123"));

        assertTrue(ex.getMessage().contains("no existe"));
    }

    // UPDATE USER

    @Test
    void should_update_user_successfully() {
        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(userRepository.save(any(User.class))).thenReturn(user);
        UserResponse response = service.updateUser(user.getUsername(), updateRequest);

        assertNotNull(response);
        assertEquals(user.getUsername(), response.getUsername());
        assertEquals(updateRequest.getName(), response.getName());
        assertEquals(updateRequest.getSurname(), response.getSurname());
    }

    @Test
    void should_update_user_username_successfully() {
        String newUsername = "newUsername";
        updateRequest.setUsername(newUsername);

        when(userRepository.findByUsername(any())).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername(newUsername)).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        UserResponse response = service.updateUser(user.getUsername(), updateRequest);

        assertNotNull(response);
        assertEquals(newUsername, response.getUsername());
    }

}
