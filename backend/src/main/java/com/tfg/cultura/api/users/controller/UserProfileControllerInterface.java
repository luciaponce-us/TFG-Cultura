package com.tfg.cultura.api.users.controller;

import com.tfg.cultura.api.core.validation.annotations.ValidImage;
import com.tfg.cultura.api.users.model.dto.UserResponse;
import com.tfg.cultura.api.users.model.dto.UserUpdateRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

public interface UserProfileControllerInterface {

	@Operation(summary = "Obtener perfil", description = "Como usuario registrado, quiero poder ver los datos de mi usuario, para modificarlos si así lo deseo")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Usuario obtenido correctamente"),
			@ApiResponse(responseCode = "403", description = "Forbidden - Usuario no autenticado"),
			@ApiResponse(responseCode = "404", description = "User Not Found - No se encontró el usuario")})
	public ResponseEntity<UserResponse> getMyProfile();

	@Operation(summary = "RF-03: Modificar perfil", description = "Como usuario registrado, quiero poder modificar los datos de mi usuario, para mantener actualizada mi información")
	@ApiResponses(value = {@ApiResponse(responseCode = "200", description = "Usuario actualizado correctamente"),
			@ApiResponse(responseCode = "400", description = "Bad Request - Solicitud inválida"),
			@ApiResponse(responseCode = "403", description = "Forbidden - Usuario no autenticado"),
			@ApiResponse(responseCode = "404", description = "User Not Found - No se encontró el usuario")})
	public ResponseEntity<UserResponse> updateMyProfile(@RequestBody @Valid UserUpdateRequest request);

	@Operation(summary = "Modificar mi avatar", description = "Como usuario registrado, quiero poder modificar la imagen de mi perfil, para personalizar mi cuenta")
	public ResponseEntity<UserResponse> updateMyAvatar(
			@RequestPart(value = "avatar") @ValidImage(message = "Avatar no válido") MultipartFile avatar);

	@Operation(summary = "RF-07: Eliminación de mi usuario", description = "Como usuario registrado, quiero poder eliminar completamente mi cuenta, eliminando también mis datos del sistema, para sentirme más seguro")
	@ApiResponses(value = {@ApiResponse(responseCode = "204", description = "Usuario eliminado correctamente"),
			@ApiResponse(responseCode = "403", description = "Forbidden - Usuario no autenticado"),
			@ApiResponse(responseCode = "404", description = "User Not Found - No se encontró el usuario")})
	public ResponseEntity<Void> deleteMyProfile();
}
