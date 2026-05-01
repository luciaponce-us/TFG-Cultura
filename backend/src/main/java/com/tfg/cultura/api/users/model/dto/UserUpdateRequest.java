package com.tfg.cultura.api.users.model.dto;

import com.tfg.cultura.api.users.model.enumerators.Role;
import com.tfg.cultura.api.users.validation.annotations.ValidDni;
import com.tfg.cultura.api.users.validation.annotations.ValidPhone;

import jakarta.validation.constraints.Email;
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
public class UserUpdateRequest {
    @NotBlank(message = "Username obligatorio")
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres")
    private String username;

    @Size(min = 8, max = 64, message = "La contraseña debe tener entre 8 y 64 caracteres")
    private String password;

    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String surname;

    @Size(min = 9, max = 9, message = "El DNI debe tener 9 caracteres")
    @ValidDni(message = "El DNI no es válido", required = false)
    private String dni;

    @Size(min = 9, max = 15, message = "El teléfono debe tener entre 9 y 15 caracteres")
    @ValidPhone(message = "El teléfono no es válido", required = false)
    private String phone;

    @Size(min = 5, max = 254, message = "El email debe tener entre 5 y 254 caracteres")
    @Email(message = "El email no es válido")
    private String email;

    private boolean active;

    private Role role;
}
