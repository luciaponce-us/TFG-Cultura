package com.tfg.cultura.api.users.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import com.tfg.cultura.api.users.model.dto.UserLoginRequest;
import com.tfg.cultura.api.users.model.dto.UserRegisterRequest;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.service.UserAuthService;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users - Auth", description = "Autenticación de usuarios")
public class UserAuthController {

    private final UserAuthService userService;

    public UserAuthController(UserAuthService userService) {
        this.userService = userService;
    }

    @Operation(summary = "RF-01: Registrar usuarios", description = "Como usuario, quiero poder solicitar mi registro en el sistema, para poder iniciar sesión.")
    @PostMapping(value = "/register", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Registro correcto"),
            @ApiResponse(responseCode = "409", description = "RN-01: DNI único por usuario / RN-02: Apodo único por usuario"),
            @ApiResponse(responseCode = "400", description = "Los datos introducidos no son válidos")
    })
    public ResponseEntity<UserResponse> register(
            @Valid @Parameter(description = "Datos del usuario en JSON", content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE)) @RequestPart("user") UserRegisterRequest request,
            @RequestPart(value = "avatar", required = false) MultipartFile avatar,
            @RequestPart(value = "paymentReceipt", required = true) MultipartFile paymentReceipt) {

        UserResponse user = userService.register(request, avatar, paymentReceipt);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(user);

    }

    @Operation(summary = "RF-02: Iniciar sesión", description = "Como usuario registrado, quiero poder iniciar sesión usando las credenciales con las que me registré previamente, para poder acceder a las funciones del sistema que requieran de autenticación")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Login correcto"),
            @ApiResponse(responseCode = "401", description = "RN-04: Inicio de sesión - Unauthorized - Credenciales inválidas"),
            @ApiResponse(responseCode = "403", description = "RN-04: Inicio de sesión - User Disabled"),
            @ApiResponse(responseCode = "404", description = "RN-04: Inicio de sesión - User Not Found"),
    })
    @PostMapping("/login")
    public ResponseEntity<String> login(@Valid @RequestBody UserLoginRequest request) {
        String token = userService.login(request);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(token);
    }

    @Operation(summary = "RF-05: Aprobar el registro de un usuario", description = "Como colaborador/encargado/secretario/coordinador, quiero poder aprobar o rechazar el registro de un usuario, para revisar que la carta de pago sea auténtica y pertenezca al usuario que solicita registrarse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro aprobado/rechazado correctamente"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - El usuario no está autenticado"),
            @ApiResponse(responseCode = "403", description = "Forbidden - El usuario no tiene permisos para aprobar registros o activar ese usuario"),
            @ApiResponse(responseCode = "404", description = "User Not Found - No se encontró el usuario a aprobar/rechazar")
    })
    @PutMapping("/{id}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable String id) {
        UserResponse response = userService.activateUser(id);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

}
