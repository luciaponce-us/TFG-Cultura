package com.tfg.cultura.api.users.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.Indexed;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import com.tfg.cultura.api.users.validation.annotations.ValidPhone;
import com.tfg.cultura.api.users.validation.annotations.ValidDni;

import org.springframework.data.annotation.CreatedDate;

import com.tfg.cultura.api.users.model.enumerators.Role;

@Document(collection = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    private String id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    @Size(min = 3, max = 20, message = "El nombre de usuario debe tener entre 3 y 20 caracteres")
    @Indexed(unique = true)
    private String username;

    @NotBlank(message = "La contraseña es obligatoria")
    private String password;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 2, max = 50, message = "El nombre debe tener entre 2 y 50 caracteres")
    private String name;

    @NotBlank(message = "El apellido es obligatorio")
    @Size(min = 2, max = 50, message = "El apellido debe tener entre 2 y 50 caracteres")
    private String surname;

    @NotBlank(message = "El DNI es obligatorio")
    @Size(min = 9, max = 9, message = "El DNI debe tener 9 caracteres")
    @ValidDni(message = "El DNI no es válido")
    @Indexed(unique = true)
    private String dni;

    @NotBlank(message = "El teléfono es obligatorio")
    @Size(min = 9, max = 15, message = "El teléfono debe tener entre 9 y 15 caracteres")
    @ValidPhone(message = "El teléfono no es válido")
    private String phone;

    @Size(min = 5, max = 254, message = "El email debe tener entre 5 y 254 caracteres")
    @Email(message = "El email no es válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @Pattern(regexp = "^https://res\\.cloudinary\\.com/[^/]+/(image|raw|video)/upload/.+", message = "URL de Cloudinary no válida")
    @Builder.Default
    private String avatar = "https://res.cloudinary.com/dubz79y98/image/upload/v1776288595/avatar_placeholder_dreac3.png";

    @Pattern(regexp = "^https://res\\.cloudinary\\.com/[^/]+/(image|raw|video)/upload/.+", message = "URL de Cloudinary no válida")
    @NotBlank(message = "La carta de pago es obligatoria")
    private String paymentReceipt;

    @Builder.Default
    @NotBlank(message = "El campo de activación es obligatorio")
    private boolean active = false;

    @Builder.Default
    @NotBlank(message = "El rol es obligatorio")
    private Role role = Role.SOCIO;

    @CreatedDate
    private LocalDateTime createdAt;
}
