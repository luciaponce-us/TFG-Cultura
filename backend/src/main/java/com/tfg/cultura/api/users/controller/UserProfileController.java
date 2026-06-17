package com.tfg.cultura.api.users.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.model.dto.UserUpdateRequest;
import com.tfg.cultura.api.users.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users/profile")
@Tag(name = "Users - Profile", description = "Gestiona tu perfil de usuario")
@RequiredArgsConstructor
public class UserProfileController implements UserProfileControllerInterface {
    private final UserService userService;

    @Override
    @GetMapping
    public ResponseEntity<UserResponse> getMyProfile() {
        UserResponse response = userService.getProfile();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Override
    @PutMapping
    public ResponseEntity<UserResponse> updateMyProfile(@RequestBody @Valid UserUpdateRequest request) {
        UserResponse response = userService.updateProfile(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Override
    @PutMapping(value="/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateMyAvatar(@RequestPart(value = "avatar") MultipartFile avatar) {
        UserResponse response = userService.updateCurrentUserAvatar(avatar);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Override
    @DeleteMapping
    public ResponseEntity<Void> deleteMyProfile() {
        userService.deleteProfile();

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
