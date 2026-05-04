package com.tfg.cultura.api.users.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;

import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.model.dto.UserUpdateRequest;
import com.tfg.cultura.api.users.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users - CRUD", description = "Gestión de usuarios")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Obtener información de un usuario concreto", description = "Como colaborador/encargado/secretario/coordinador, quiero poder consultar la información de un usuario concreto, para poder revisar su información personal y realizar las operaciones CRUD")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario obtenido correctamente"),
            @ApiResponse(responseCode = "403", description = "Forbidden - El usuario no tiene permisos para leer usuarios"),
            @ApiResponse(responseCode = "404", description = "User Not Found - No se encontró el usuario a aprobar/rechazar")
    })
    @GetMapping("/{username}")
    @PreAuthorize("hasAnyRole('SECRETARIO', 'COORDINADOR')")
    public ResponseEntity<UserResponse> getUser(@PathVariable String username) {
        UserResponse response = userService.getUser(username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

    @Operation(summary = "Editar un usuario concreto (RF-04)", description = "Como secretario/coordinador, quiero poder realizar las operaciones CRUD (crear, leer, actualizar y eliminar) la información sobre los usuarios, para tener control total sobre la gestión de usuarios")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
            @ApiResponse(responseCode = "403", description = "Forbidden - El usuario no tiene permisos para actualizar usuarios"),
            @ApiResponse(responseCode = "404", description = "User Not Found - No se encontró el usuario"),
            @ApiResponse(responseCode = "409", description = "User Already Exists - El username y/o el DNI están en uso")
    })
    @PutMapping("/{username}")
    @PreAuthorize("hasAnyRole('SECRETARIO', 'COORDINADOR')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable String username, @RequestBody @Valid UserUpdateRequest request) {
        UserResponse response = userService.updateUser(username, request);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}
