package com.tfg.cultura.api.catalog.controller;

import com.tfg.cultura.api.catalog.model.dto.MovieRequest;
import com.tfg.cultura.api.catalog.model.dto.MovieResponse;
import com.tfg.cultura.api.catalog.service.MovieService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog/movies")
@Tag(name = "Catalog - Movies", description = "Gestión de películas")
public class MovieController extends AbstractItemController<MovieRequest, MovieResponse, MovieService> {

	public MovieController(MovieService movieService) {
		super(movieService);
	}

}
