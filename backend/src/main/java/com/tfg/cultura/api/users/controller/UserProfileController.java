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

import com.tfg.cultura.api.users.model.dto.UserProfileUpdateRequest;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users/profile")
@Tag(name = "Users - Profile", description = "Gestiona tu perfil de usuario")
@RequiredArgsConstructor
public class UserProfileController {
    private final UserService userService;

    @Operation(summary = "Obtener perfil", description = "Como usuario registrado, quiero poder ver los datos de mi usuario, para modificarlos si así lo deseo")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario obtenido correctamente"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Usuario no autenticado"),
            @ApiResponse(responseCode = "404", description = "User Not Found - No se encontró el usuario")
    })
    @GetMapping
    public ResponseEntity<UserResponse> getMyProfile() {
        UserResponse response = userService.getProfile();

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "RF-03: Modificar perfil", description = "Como usuario registrado, quiero poder modificar los datos de mi usuario, para mantener actualizada mi información")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Bad Request - Solicitud inválida"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Usuario no autenticado"),
            @ApiResponse(responseCode = "404", description = "User Not Found - No se encontró el usuario")
    })
    @PutMapping
    public ResponseEntity<UserResponse> updateMyProfile(@RequestBody @Valid UserProfileUpdateRequest request) {
        UserResponse response = userService.updateProfile(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "Modificar mi avatar", description = "Como usuario registrado, quiero poder modificar la imagen de mi perfil, para personalizar mi cuenta")
    @PutMapping(value="/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserResponse> updateMyAvatar(@RequestPart(value = "avatar") MultipartFile avatar) {
        UserResponse response = userService.updateCurrentUserAvatar(avatar);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "RF-07: Eliminación de mi usuario", description = "Como usuario registrado, quiero poder eliminar completamente mi cuenta, eliminando también mis datos del sistema, para sentirme más seguro")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
            @ApiResponse(responseCode = "403", description = "Forbidden - Usuario no autenticado"),
            @ApiResponse(responseCode = "404", description = "User Not Found - No se encontró el usuario")
    })
    @DeleteMapping
    public ResponseEntity<Void> deleteMyProfile() {
        userService.deleteProfile();

        return ResponseEntity
                .status(HttpStatus.NO_CONTENT)
                .build();
    }

}
