package com.tfg.cultura.api.users.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserLoginRequest {
	@NotBlank(message = "El nombre de usuario es obligatorio")
	@Size(max = 20, message = "El nombre de usuario debe tener 20 caracteres como máximo")
	private String username;

	@NotBlank(message = "La contraseña es obligatoria")
	@Size(max = 64, message = "La contraseña debe tener 64 caracteres como máximo")
	private String password;

}
