package com.tfg.cultura.api.catalog.repository;

import java.util.Optional;

import com.tfg.cultura.api.catalog.model.Movie;
import com.tfg.cultura.api.catalog.model.enumerators.Format;

public interface MovieRepository extends ItemRepository<Movie> {
    Optional<Movie> findByNameAndFormat(String name, Format format);
}
