package com.tfg.cultura.api.catalog.model;

import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.catalog.validation.annotations.ValidIsbn;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Document(collection = "books")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Book extends Item {

	@NotBlank(message = "El autor es obligatorio")
	@Size(max = 100, message = "El nombre del autor no puede superar 100 caracteres")
	private String author;

	@ValidIsbn
	@NotBlank(message = "El ISBN es obligatorio")
	private String isbn;

	@NotBlank(message = "El tipo es obligatorio")
	private BookType type;

	@DocumentReference
	private Saga saga;

}
