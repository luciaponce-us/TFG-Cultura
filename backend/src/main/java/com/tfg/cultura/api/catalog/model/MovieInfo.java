package com.tfg.cultura.api.catalog.model;

import java.time.LocalDate;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MovieInfo {
    private LocalDate releaseDate;
    private String trailerUrl;
    private Saga saga;
    
}
