package com.tfg.cultura.api.catalog.model;

import java.time.LocalDate;

import com.tfg.cultura.api.catalog.model.enumerators.SeriesStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SeriesInfo {
    @Past(message = "La fecha de lanzamiento debe ser una fecha pasada")
    @NotNull(message = "La fecha de estreno es obligatoria")
    private LocalDate releaseDate;

    @Min(value = 1, message = "El número de temporadas debe ser mayor o igual a 1")
    @Max(value = 1000, message = "El número de temporadas debe ser menor o igual a 1000")
    @NotNull(message = "El número de temporadas es obligatorio")
    private Integer numberOfSeasons;

    @NotNull(message = "El estado de la serie es obligatorio")
    private SeriesStatus status;

}
