package com.tfg.cultura.api.catalog.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Season {

    @Min(value = 0, message = "El número de temporada debe ser mayor o igual a 0")
    @NotNull(message = "El número de temporada es obligatorio")
    private Integer seasonNumber;

    @Min(value = 0, message = "El número de parte de temporada debe ser mayor o igual a 0")
    private Integer seasonPart;

    @Size(max = 500, message = "La URL del tráiler no puede superar los 500 caracteres")
    private String trailerUrl;

}
