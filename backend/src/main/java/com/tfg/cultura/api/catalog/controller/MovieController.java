package com.tfg.cultura.api.catalog.controller;

import com.tfg.cultura.api.catalog.model.dto.MovieRequest;
import com.tfg.cultura.api.catalog.model.dto.MovieResponse;
import com.tfg.cultura.api.catalog.service.MovieService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Set;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog/movies")
@Tag(name = "Catalog - Movies", description = "Gestión de películas")
public class MovieController extends AbstractItemController<MovieRequest, MovieResponse, MovieService> {

	public MovieController(MovieService movieService) {
		super(movieService);
	}

	@GetMapping("/saga/{sagaId}")
	public ResponseEntity<Set<MovieResponse>> getAllMoviesBySaga(@PathVariable String sagaId) {
		Set<MovieResponse> response = service.getAllMoviesBySagaId(sagaId);
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
