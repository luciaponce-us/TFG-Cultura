package com.tfg.cultura.api.catalog.model;

import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.DocumentReference;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovieInfo {

    @Past(message = "La fecha de lanzamiento debe ser una fecha pasada")
    @NotNull(message = "La fecha de estreno es obligatoria")
    private LocalDate releaseDate;

    @Size(max = 500, message = "La URL del tráiler no puede superar los 500 caracteres")
    private String trailerUrl;

    @DocumentReference
    private Saga saga;
    
}
