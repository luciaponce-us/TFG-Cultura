package com.tfg.cultura.api.users.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.exception.UsersExceptionHandler;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.service.UserService;
import com.tfg.cultura.api.utils.BaseControllerTest;

class UserControllerTest extends BaseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserService userService;

    private static final String BASE_URL = "/api/users";
    private static final String USER_URL = BASE_URL + "/{id}";

    private UserResponse userResponse;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        UserController controller = new UserController(userService);
        mockMvc = buildMockMvc(controller, UsersExceptionHandler.class);

        initTestData();
    }

    private void initTestData() {
        userResponse = UserFactory.validUserResponse();
    }

    // ================ GET USER ================

    @Test
    void get_user_success() throws Exception {
        when(userService.getUser(anyString())).thenReturn(userResponse);

        mockMvc.perform(get(USER_URL, "123"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userResponse.getUsername()));
    }

    @Test
    void get_user_fail_unexisting_user() throws Exception {
        String message = "El usuario no existe";
        UserNotFoundException ex = new UserNotFoundException(message);
        when(userService.getUser(anyString())).thenThrow(ex);

        mockMvc.perform(get(USER_URL, "123"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(message));
    }
    
}
