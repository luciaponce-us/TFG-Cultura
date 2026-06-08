package com.tfg.cultura.api.users.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RequestBody;

import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.model.dto.UserUpdateRequest;
import com.tfg.cultura.api.users.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import com.tfg.cultura.api.users.model.enumerators.Role;

@RestController
@RequestMapping("/api/users")
@Tag(name = "Users - CRUD", description = "Gestión de usuarios")
public class UserController {

	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@Operation(summary = "Obtener información de todos los usuarios", description = "Como colaborador/encargado/secretario/coordinador, quiero poder consultar la información de todos los usuarios, para poder revisar su información personal y realizar las operaciones CRUD")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Usuarios obtenidos correctamente"),
			@ApiResponse(responseCode = "403", description = "Forbidden - El usuario no tiene permisos para leer usuarios")
	})
	@GetMapping
	public ResponseEntity<Page<UserResponse>> getAllUsers(
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) Role role,
        	@RequestParam(required = false) Boolean active,
        	@RequestParam(required = false) String name
		) {
		Page<UserResponse> response = userService.getAllUsers(page, size, role, active, name);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(response);
	}

	@Operation(summary = "Obtener información de un usuario concreto", description = "Como colaborador/encargado/secretario/coordinador, quiero poder consultar la información de un usuario concreto, para poder revisar su información personal y realizar las operaciones CRUD")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Usuario obtenido correctamente"),
			@ApiResponse(responseCode = "403", description = "Forbidden - El usuario no tiene permisos para leer usuarios"),
			@ApiResponse(responseCode = "404", description = "User Not Found - No se encontró el usuario a aprobar/rechazar")
	})
	@GetMapping("/{username}")
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
	public ResponseEntity<UserResponse> updateUser(@PathVariable String username,
			@RequestBody @Valid UserUpdateRequest request) {
		UserResponse response = userService.updateUser(username, request);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(response);
	}

	@Operation(summary = "Editar un usuario concreto (RF-04)", description = "Como secretario/coordinador, quiero poder realizar las operaciones CRUD (crear, leer, actualizar y eliminar) la información sobre los usuarios, para tener control total sobre la gestión de usuarios")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
			@ApiResponse(responseCode = "403", description = "Forbidden - El usuario no tiene permisos para actualizar usuarios"),
			@ApiResponse(responseCode = "404", description = "User Not Found - No se encontró el usuario")
	})
	@PutMapping(value="/{username}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<UserResponse> updateUserAvatar(@PathVariable String username,
			@RequestPart(value = "avatar") MultipartFile avatar) {
		UserResponse response = userService.updateUserAvatar(username, avatar);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(response);
	}

	@Operation(summary = "Eliminar un usuario concreto (RF-04)", description = "Como secretario/coordinador, quiero poder realizar las operaciones CRUD (crear, leer, actualizar y eliminar) la información sobre los usuarios, para tener control total sobre la gestión de usuarios")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
			@ApiResponse(responseCode = "403", description = "Forbidden - El usuario no tiene permisos para eliminar usuarios"),
			@ApiResponse(responseCode = "404", description = "User Not Found - No se encontró el usuario")
	})
	@DeleteMapping("/{username}")
	public ResponseEntity<Void> deleteUser(@PathVariable String username) {
		userService.deleteUser(username);
		return ResponseEntity
				.noContent()
				.build();
	}

	@Operation(summary = "RF-05: Aprobar el registro de un usuario", description = "Como colaborador/encargado/secretario/coordinador, quiero poder aprobar o rechazar el registro de un usuario, para revisar que la carta de pago sea auténtica y pertenezca al usuario que solicita registrarse")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Registro aprobado/rechazado correctamente"),
            @ApiResponse(responseCode = "401", description = "Unauthorized - El usuario no está autenticado"),
            @ApiResponse(responseCode = "403", description = "Forbidden - El usuario no tiene permisos para aprobar registros o activar ese usuario"),
            @ApiResponse(responseCode = "404", description = "User Not Found - No se encontró el usuario a aprobar/rechazar")
    })
    @PutMapping("/{username}/activate")
    public ResponseEntity<UserResponse> activateUser(@PathVariable String username) {
        UserResponse response = userService.activateUser(username);
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(response);
    }

	@PutMapping("/{username}/deactivate")
	public ResponseEntity<UserResponse> deactivateUser(@PathVariable String username) {
		UserResponse response = userService.deactivateUser(username);
		return ResponseEntity
				.status(HttpStatus.OK)
				.body(response);
	}

}
