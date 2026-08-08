package com.tfg.cultura.api.catalog.model;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tfg.cultura.api.catalog.model.enumerators.Format;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Document(collection = "movies")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class Movie extends Item {
    
    @NotNull(message = "El formato es obligatorio")
    private Format format;

    @NotNull(message = "El número de discos es obligatorio")
    @Min(value = 1, message = "El número de discos debe ser mayor o igual a 1")
    @Builder.Default
    private Integer numberOfDiscs = 1;

    @NotNull(message = "La información de la película es obligatoria")
    private MovieInfo movieInfo;
    
}
