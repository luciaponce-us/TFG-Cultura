package com.tfg.cultura.api.catalog.model.dto;

import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.catalog.validation.annotations.ValidIsbn;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BookRequest extends ItemRequest {
	@NotBlank(message = "El autor es obligatorio")
	@Size(max = 100, message = "El nombre del autor no puede superar 100 caracteres")
	private String author;

	@ValidIsbn
	@NotBlank(message = "El ISBN es obligatorio")
	private String isbn;

	@Size(max = 50, message = "El nombre de la saga no puede superar 50 caracteres")
	private String sagaName;

	@NotNull(message = "El tipo de libro es obligatorio")
	private BookType type;

}
