package com.tfg.cultura.api.catalog.model.dto;

import com.tfg.cultura.api.catalog.model.enumerators.Format;
import com.tfg.cultura.api.core.validation.annotations.ValidYouTubeEmbedUrl;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
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
public class MovieRequest extends ItemRequest {

	@NotNull(message = "El formato es obligatorio")
	private Format format;

	@NotNull(message = "El número de discos es obligatorio")
	@Min(value = 1, message = "El número de discos debe ser mayor o igual a 1")
	private Integer numberOfDiscs;

	@Past(message = "La fecha de lanzamiento no puede ser futura")
	private LocalDate releaseDate;

	@Size(max = 500, message = "La URL del tráiler no puede superar los 500 caracteres")
	@ValidYouTubeEmbedUrl(message = "La URL del tráiler debe ser una URL de YouTube embebida válida. Ejemplo: https://www.youtube.com/embed/nsNFOn_4i4E")
	private String trailerUrl;

	private String sagaName;

}
