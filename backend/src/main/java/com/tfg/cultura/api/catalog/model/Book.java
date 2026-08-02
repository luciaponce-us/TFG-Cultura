package com.tfg.cultura.api.catalog.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.tfg.cultura.api.catalog.model.enumerators.BookType;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Document(collection = "books")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Book extends Item {
    @NotBlank(message = "El autor es obligatorio")
    private String author;

    @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$", message = "El ISBN debe ser un número válido de 10 o 13 dígitos")
    @NotBlank(message = "El ISBN es obligatorio")
    private String isbn;

    private String saga;
    
    @Min(value = 1, message = "El número de libro en la saga debe ser mayor o igual a 1")
    private Integer number;

    @NotBlank(message = "El tipo es obligatorio")
    private BookType type;

    @DocumentReference
    private Book prequel;
    @DocumentReference
    private Book sequel;

}
