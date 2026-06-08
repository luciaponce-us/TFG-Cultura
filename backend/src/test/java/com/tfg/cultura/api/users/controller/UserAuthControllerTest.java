package com.tfg.cultura.api.users.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.test.web.servlet.MockMvc;

import com.tfg.cultura.api.users.exception.UserAlreadyExistsException;
import com.tfg.cultura.api.users.exception.UserNotFoundException;
import com.tfg.cultura.api.users.exception.UsersExceptionHandler;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.dto.UserLoginRequest;
import com.tfg.cultura.api.users.model.dto.UserRegisterRequest;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.service.UserAuthService;
import com.tfg.cultura.api.utils.BaseControllerTest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserAuthControllerTest extends BaseControllerTest {

    private MockMvc mockMvc;

    @Mock
    private UserAuthService userService;

    private static final String BASE_URL = "/api/users/auth";
    private static final String REGISTER_URL = BASE_URL + "/register";
    private static final String LOGIN_URL = BASE_URL + "/login";

    private UserRegisterRequest registerRequest;
    private UserLoginRequest loginRequest;
    private UserResponse userResponse;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        UserAuthController controller = new UserAuthController(userService);
        mockMvc = buildMockMvc(controller, UsersExceptionHandler.class);

        initTestData();
    }

    private void initTestData() {
        registerRequest = UserFactory.validUserRegisterRequest();
        userResponse = UserFactory.validUserResponse();
        loginRequest = UserFactory.loginRequest();
    }

    @Test
    void register_success() throws Exception {
        when(userService.register(any(), any(), any())).thenReturn(userResponse);

        mockMvc.perform(multipart(REGISTER_URL)
                .file(userPart(registerRequest))
                .file(pdfPart()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.username").value(registerRequest.getUsername()));
    }

    @Test
    void register_fail_user_already_exists() throws Exception {
        UserAlreadyExistsException ex = new UserAlreadyExistsException("El nombre de usuario ya existe");

        when(userService.register(any(), any(), any())).thenThrow(ex);

        mockMvc.perform(multipart(REGISTER_URL)
                .file(userPart(registerRequest))
                .file(pdfPart()))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").exists());
    }

    @Test
    void register_fail_invalid_data() throws Exception {
        registerRequest.setEmail("invalid-email");

        mockMvc.perform(multipart(REGISTER_URL)
                .file(userPart(registerRequest))
                .file(pdfPart()))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").exists());
    }

    // ====== LOGIN ========

    @Test
    void login_success() throws Exception {
        String token = "token123";
        when(userService.login(any())).thenReturn(token);

        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(loginRequest)))
                .andExpect(status().isOk())
                .andExpect(content().string(token));

    }

    @Test
    void login_fail_invalid_credentials() throws Exception {
        BadCredentialsException ex = new BadCredentialsException("Credenciales inválidas");
        when(userService.login(any())).thenThrow(ex);

        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(loginRequest)))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.message").value(ex.getMessage()));

    }

    @Test
    void login_fail_user_not_found() throws Exception {
        String username = loginRequest.getUsername();
        String message = "El usuario con username " + username + " no existe";
        UserNotFoundException ex = new UserNotFoundException(message);
        when(userService.login(any())).thenThrow(ex);

        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(loginRequest)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value(message));
    }

    @Test
    void login_fail_user_disabled() throws Exception {
        String message = "El usuario está desactivado";
        DisabledException ex = new DisabledException(message);
        when(userService.login(any())).thenThrow(ex);

        mockMvc.perform(post(LOGIN_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(loginRequest)))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value(message));
    }

    // Helpers

    private MockMultipartFile userPart(Object obj) throws Exception {
        return new MockMultipartFile(
                "user",
                "",
                "application/json",
                toJson(obj).getBytes());
    }

    private MockMultipartFile pdfPart() {
        return new MockMultipartFile(
                "paymentReceipt",
                "test.pdf",
                "application/pdf",
                "dummy pdf content".getBytes());
    }

}