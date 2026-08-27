package com.tfg.cultura.api.catalog.model;

import com.tfg.cultura.api.core.validation.annotations.ValidYouTubeEmbedUrl;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Season {

	@Min(value = 0, message = "El número de temporada debe ser mayor o igual a 0")
	@Max(value = 1000, message = "El número de temporada debe ser menor o igual a 1000")
	@NotNull(message = "El número de temporada es obligatorio")
	private Integer seasonNumber;

	@Min(value = 0, message = "El número de parte de temporada debe ser mayor o igual a 0")
	@Max(value = 10, message = "El número de parte de temporada debe ser menor o igual a 10")
	private Integer seasonPart;

	@Size(max = 500, message = "La URL del tráiler no puede superar los 500 caracteres")
	@ValidYouTubeEmbedUrl(message = "La URL del tráiler debe ser una URL de YouTube embebida válida. Ejemplo: https://www.youtube.com/embed/nsNFOn_4i4E")
	private String trailerUrl;

}
