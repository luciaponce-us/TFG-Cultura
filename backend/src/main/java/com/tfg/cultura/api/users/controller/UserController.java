package com.tfg.cultura.api.users.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

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
            @ApiResponse(responseCode = "401", description = "Unauthorized - El usuario no está autenticado"),
            @ApiResponse(responseCode = "403", description = "Forbidden - El usuario no tiene permisos para aprobar registros"),
            @ApiResponse(responseCode = "404", description = "User Not Found - No se encontró el usuario a aprobar/rechazar")
    })
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('COLABORADOR', 'ENCARGADO', 'SECRETARIO', 'COORDINADOR')")
    public ResponseEntity<UserResponse> getById(@PathVariable String id) {
        UserResponse response = userService.getUserById(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }
    
}
