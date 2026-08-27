package com.tfg.cultura.api.categories.model;

import com.tfg.cultura.api.core.validation.annotations.ValidHexColor;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

	@Id
	private String id;

	@NotBlank(message = "El nombre es obligatorio")
	@Size(min = 3, max = 20, message = "El nombre debe tener entre 3 y 20 caracteres")
	@Indexed(unique = true)
	private String name;

	@NotBlank(message = "El color es obligatorio")
	@ValidHexColor
	private String color;

}
