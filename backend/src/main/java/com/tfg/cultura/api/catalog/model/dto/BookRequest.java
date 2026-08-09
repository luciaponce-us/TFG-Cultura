package com.tfg.cultura.api.catalog.model.dto;

import com.tfg.cultura.api.catalog.model.enumerators.BookType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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

    @Pattern(regexp = "^(97(8|9))?\\d{9}(\\d|X)$", message = "El ISBN debe ser un número válido de 10 o 13 dígitos")
    @NotBlank(message = "El ISBN es obligatorio")
    private String isbn;

    private String sagaName;

    @NotNull(message = "El tipo de libro es obligatorio")
    private BookType type;
    
}
