package com.tfg.cultura.api.sections.model.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.HashSet;
import java.util.Set;
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
public class SectionCreateRequest {

	@NotBlank(message = "El nombre es obligatorio")
	@Size(min = 3, max = 50, message = "El nombre debe tener entre 3 y 50 caracteres")
	private String name;

	@NotNull
	@Size(min = 1, message = "La sección debe tener al menos un encargado")
	private Set<String> managersUsernames; // Encargados de la sección

	@Builder.Default
	@NotNull
	private Set<String> collaboratorsUsernames = new HashSet<>(); // Colaboradores de la sección

}
