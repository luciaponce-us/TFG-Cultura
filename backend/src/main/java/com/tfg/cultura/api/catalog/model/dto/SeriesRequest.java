package com.tfg.cultura.api.catalog.model.dto;

import java.time.LocalDate;
import java.util.List;

import com.tfg.cultura.api.catalog.model.Season;
import com.tfg.cultura.api.catalog.model.enumerators.Format;
import com.tfg.cultura.api.catalog.model.enumerators.SeriesStatus;

import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class SeriesRequest extends ItemRequest {
    private Format format;
    private Integer numberOfDiscs;
    private LocalDate releaseDate;
    private Integer numberOfSeasons;
    private SeriesStatus status;
    private List<Season> seasons;

}
