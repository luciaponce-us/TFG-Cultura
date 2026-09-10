package com.tfg.cultura.api.catalog.exception.series;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tfg.cultura.api.core.exception.ValidationException;

public class InvalidNumberOfSeasonsException extends ValidationException {
        private static final Logger logger = LoggerFactory.getLogger("catalogLogger");
    public InvalidNumberOfSeasonsException(Integer higherSeason, Integer maxSeason) {
        super(logger, Map.of("numberOfSeasons", String.format("La serie tiene %d temporadas, pero has añadido la temporada número %d.", maxSeason, higherSeason)));
    }
    
}
