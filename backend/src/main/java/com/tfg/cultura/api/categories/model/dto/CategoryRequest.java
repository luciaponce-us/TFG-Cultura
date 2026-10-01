package com.tfg.cultura.api.categories.model.dto;

import com.tfg.cultura.api.core.validation.annotations.ValidHexColor;

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
public class CategoryRequest {
	@NotBlank(message = "El nombre es obligatorio")
	@Size(min = 3, max = 20, message = "El nombre debe tener entre 3 y 20 caracteres")
	private String name;

	@NotBlank(message = "El color es obligatorio")
	@ValidHexColor
	private String color;
}
