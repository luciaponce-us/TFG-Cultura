package com.tfg.cultura.api.catalog.model.dto;

import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.catalog.validation.annotations.ValidIsbn;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
	private String author;

	@ValidIsbn
	@NotBlank(message = "El ISBN es obligatorio")
	private String isbn;

	private String sagaName;

	@NotNull(message = "El tipo de libro es obligatorio")
	private BookType type;

}
