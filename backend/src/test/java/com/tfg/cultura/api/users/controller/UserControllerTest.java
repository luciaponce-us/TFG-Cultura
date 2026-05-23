package com.tfg.cultura.api.users.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import com.tfg.cultura.api.users.exception.UserAlreadyExistsException;
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.exception.UsersExceptionHandler;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.model.dto.UserUpdateRequest;
import com.tfg.cultura.api.users.service.UserService;
import com.tfg.cultura.api.utils.BaseControllerTest;

class UserControllerTest extends BaseControllerTest {

    @Mock
    private UserService userService;

    private static final String BASE_URL = "/api/users";
    private static final String USER_URL = BASE_URL + "/{username}";

    private UserResponse userResponse;
    private UserUpdateRequest updateRequest;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        UserController controller = new UserController(userService);
        mockMvc = buildMockMvc(controller, UsersExceptionHandler.class);

        initTestData();
    }

    private void initTestData() {
        userResponse = UserFactory.validUserResponse();
        updateRequest = UserFactory.validUserUpdateRequest();
    }

    // ================ GET USER ================

    @Test
    void get_user_success() throws Exception {
        when(userService.getUser(anyString())).thenReturn(userResponse);

        mockMvc.perform(get(USER_URL, "username"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(userResponse.getUsername()));
    }

    @Test
    void get_user_fail_unexisting_user() throws Exception {
        String message = "El usuario no existe";
        UserNotFoundException ex = new UserNotFoundException(message);
        when(userService.getUser(anyString())).thenThrow(ex);

        mockMvc.perform(get(USER_URL, "username"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(message));
    }

    // ================ UPDATE USER ================

    @Test
    void should_update_user_successfully() throws Exception {
        String username = userResponse.getUsername();

        when(userService.updateUser(anyString(), any()))
                .thenReturn(userResponse);

        mockMvc.perform(put(USER_URL, username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username));

        verify(userService).updateUser(anyString(), any(UserUpdateRequest.class));
    }

    // ❌ 404 Not Found
    @Test
    void should_return_404_when_user_not_found() throws Exception {
        String username = "notfound";

        UserUpdateRequest request = new UserUpdateRequest();
        request.setPassword("newPassword");

        when(userService.updateUser(anyString(), any(UserUpdateRequest.class)))
                .thenThrow(new UserNotFoundException("Usuario no encontrado"));

        mockMvc.perform(put(USER_URL, username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isNotFound());
    }

    // ❌ 409 Conflict
    @Test
    void should_return_409_when_user_already_exists() throws Exception {
        String username = "testuser";

        UserUpdateRequest request = new UserUpdateRequest();
        request.setUsername("existingUser");

        when(userService.updateUser(anyString(), any(UserUpdateRequest.class)))
                .thenThrow(new UserAlreadyExistsException("Username en uso"));

        mockMvc.perform(put(USER_URL, username)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toJson(request)))
                .andExpect(status().isConflict());
    }

    // ================ DELETE USER ================

    @Test
    void should_delete_user_successfully() throws Exception {
        mockMvc.perform(delete(USER_URL, "username"))
                .andExpect(status().isNoContent());

        verify(userService).deleteUser(anyString());
    }

    // ❌ 404 Not Found
    @Test
    void should_return_404_when_delete_unexisting_user() throws Exception {
        doThrow(new UserNotFoundException("Usuario no encontrado"))
            .when(userService).deleteUser(anyString());

        mockMvc.perform(delete(USER_URL, "username"))
                .andExpect(status().isNotFound());
        
        verify(userService).deleteUser(anyString());
    }
    
    // ================ GET ALL USERS ================
    @Test
    void should_return_paginated_users() throws Exception {

        UserResponse user1 = new UserResponse(UserFactory.validUser());
        UserResponse user2 = new UserResponse(UserFactory.validUser());

        Page<UserResponse> page = new PageImpl<>(
                List.of(user1, user2),
                PageRequest.of(0, 10),
                2
        );

        when(userService.getAllUsers(0, 10)).thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                        .param("page", "0")
                        .param("size", "10")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(2))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));

        verify(userService).getAllUsers(0, 10);
    }

}
