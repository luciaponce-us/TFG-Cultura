package com.tfg.cultura.api.catalog.model;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.catalog.validation.annotations.ValidIsbn;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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

    @ValidIsbn
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
