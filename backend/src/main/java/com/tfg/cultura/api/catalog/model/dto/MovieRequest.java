package com.tfg.cultura.api.catalog.model.dto;

import java.time.LocalDate;

import com.tfg.cultura.api.catalog.model.enumerators.Format;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
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
public class MovieRequest extends ItemRequest {

    @NotNull(message = "El formato es obligatorio")
    private Format format;

    @NotNull(message = "El número de discos es obligatorio")
    @Min(value = 1, message = "El número de discos debe ser mayor o igual a 1")
    private Integer numberOfDiscs;

    @Past(message = "La fecha de lanzamiento debe ser una fecha pasada")
    private LocalDate releaseDate;

    @Size(max = 500, message = "La URL del tráiler no puede superar los 500 caracteres")
    private String trailerUrl;

    private String sagaName;

}
