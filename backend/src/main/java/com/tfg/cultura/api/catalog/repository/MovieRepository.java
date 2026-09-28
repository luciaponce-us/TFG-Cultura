package com.tfg.cultura.api.catalog.repository;

import com.tfg.cultura.api.catalog.model.Movie;
import com.tfg.cultura.api.catalog.model.enumerators.Format;
import java.util.Optional;
import java.util.Set;

public interface MovieRepository extends AbstractItemRepository<Movie> {
	Optional<Movie> findByNameAndFormat(String name, Format format);

	Set<Movie> findAllByMovieInfoSagaId(String sagaId);
}
