package com.tfg.cultura.api.catalog.model;

import com.tfg.cultura.api.core.validation.annotations.ValidYouTubeEmbedUrl;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.Builder;
import lombok.Getter;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

@Getter
@Builder
public class MovieInfo {

	@Past(message = "La fecha de lanzamiento debe ser una fecha pasada")
	@NotNull(message = "La fecha de estreno es obligatoria")
	private LocalDate releaseDate;

	@Size(max = 500, message = "La URL del tráiler no puede superar los 500 caracteres")
	@ValidYouTubeEmbedUrl(message = "La URL del tráiler debe ser una URL de YouTube embebida válida. Ejemplo: https://www.youtube.com/embed/nsNFOn_4i4E")
	private String trailerUrl;

	@DocumentReference
	private Saga saga;

}
