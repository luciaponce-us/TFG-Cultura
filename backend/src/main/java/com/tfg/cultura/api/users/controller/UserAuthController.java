package com.tfg.cultura.api.users.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.tfg.cultura.api.users.model.dto.UserLoginRequest;
import com.tfg.cultura.api.users.model.dto.UserRegisterRequest;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.service.UserAuthService;

@RestController
@RequestMapping("/api/users/auth")
@RequiredArgsConstructor
@Tag(name = "Users - Auth", description = "Autenticación de usuarios")
public class UserAuthController implements UserAuthControllerInterface {

    private final UserAuthService userService;

    @Override
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> register(
            @Valid @Parameter(description = "Datos del usuario en JSON", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) @RequestPart("user") UserRegisterRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar,
            @RequestPart(value = "paymentReceipt", required = true) MultipartFile paymentReceipt) {

        UserResponse user = userService.register(request, avatar, paymentReceipt);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);

    }

    @Override
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginRequest request) {
        String token = userService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(token);
    }

}
