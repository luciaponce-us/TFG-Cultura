package com.tfg.cultura.api.catalog.repository;

import com.tfg.cultura.api.catalog.model.Movie;
import com.tfg.cultura.api.catalog.model.enumerators.Format;
import java.util.Optional;

public interface MovieRepository extends AbstractItemRepository<Movie> {
	Optional<Movie> findByNameAndFormat(String name, Format format);
}
