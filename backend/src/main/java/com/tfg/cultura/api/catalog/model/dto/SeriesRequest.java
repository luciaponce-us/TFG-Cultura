package com.tfg.cultura.api.catalog.model.dto;

import java.time.LocalDate;
import java.util.List;

import com.tfg.cultura.api.catalog.model.Season;
import com.tfg.cultura.api.catalog.model.enumerators.Format;
import com.tfg.cultura.api.catalog.model.enumerators.SeriesStatus;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class SeriesRequest extends ItemRequest {
    @NotNull(message = "El formato es obligatorio")
    private Format format;

    @NotNull(message = "El número de discos es obligatorio")
    @Min(value = 1, message = "El número de discos debe ser mayor o igual a 1")
    private Integer numberOfDiscs;

    @Past(message = "La fecha de lanzamiento debe ser una fecha pasada")
    @NotNull(message = "La fecha de estreno es obligatoria")
    private LocalDate releaseDate;

    @Min(value = 1, message = "El número de temporadas debe ser mayor o igual a 1")
    @Max(value = 1000, message = "El número de temporadas debe ser menor o igual a 1000")
    @NotNull(message = "El número de temporadas es obligatorio")
    private Integer numberOfSeasons;

    @NotNull(message = "El estado de la serie es obligatorio")
    private SeriesStatus status;

    @NotNull(message = "La lista de temporadas es obligatoria")
    @NotEmpty(message = "La lista de temporadas no puede estar vacía")
    private List<Season> seasons;

}
