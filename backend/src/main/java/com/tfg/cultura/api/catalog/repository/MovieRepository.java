package com.tfg.cultura.api.catalog.repository;

import com.tfg.cultura.api.catalog.model.Movie;
import com.tfg.cultura.api.catalog.model.enumerators.Format;

public interface MovieRepository extends ItemRepository<Movie> {
    boolean existsByNameAndFormat(String name, Format format);
}
