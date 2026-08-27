package com.tfg.cultura.api.catalog.model;

import com.tfg.cultura.api.catalog.model.enumerators.Platform;
import com.tfg.cultura.api.core.validation.annotations.ValidYouTubeEmbedUrl;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "videogames")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class VideoGame extends Item {

	@NotNull(message = "La plataforma es obligatoria")
	private Platform platform;

	@NotNull(message = "La fecha de lanzamiento es obligatoria")
	@PastOrPresent(message = "La fecha de lanzamiento no puede ser futura")
	private LocalDate releaseDate;

	@Size(max = 280, message = "La URL del tráiler no puede tener más de 280 caracteres")
	@ValidYouTubeEmbedUrl(message = "La URL del tráiler debe ser una URL de YouTube embebida válida. Ejemplo: https://www.youtube.com/embed/nsNFOn_4i4E")
	private String trailerUrl;

}
