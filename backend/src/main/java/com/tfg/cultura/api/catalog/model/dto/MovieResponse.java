package com.tfg.cultura.api.catalog.model.dto;

import com.tfg.cultura.api.catalog.model.Movie;
import com.tfg.cultura.api.catalog.model.Saga;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class MovieResponse extends ItemResponse {
	private String format;
	private Integer numberOfDiscs;
	private LocalDate releaseDate;
	private String trailerUrl;
	private Saga saga;

	public MovieResponse(Movie movie) {
		super(movie);

		this.format = movie.getFormat().name();
		this.numberOfDiscs = movie.getNumberOfDiscs();
		this.releaseDate = movie.getMovieInfo().getReleaseDate();
		this.trailerUrl = movie.getMovieInfo().getTrailerUrl();
		this.saga = movie.getMovieInfo().getSaga();
	}

}
