package com.tfg.cultura.api.catalog.model.dto;

import java.time.LocalDate;

import com.tfg.cultura.api.catalog.model.enumerators.Platform;
import com.tfg.cultura.api.core.validation.annotations.ValidYouTubeEmbedUrl;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@SuperBuilder
public class VideoGameRequest extends ItemRequest {

    @NotNull(message = "La plataforma es obligatoria")
    private Platform platform;

    @NotNull(message = "La fecha de lanzamiento es obligatoria")
    @PastOrPresent(message = "La fecha de lanzamiento no puede ser futura")
    private LocalDate releaseDate;

    @Size(max = 280, message = "La URL del tráiler no puede tener más de 280 caracteres")
    @ValidYouTubeEmbedUrl(message = "La URL del tráiler debe ser una URL de YouTube embebida válida. Ejemplo: https://www.youtube.com/embed/nsNFOn_4i4E")
    private String trailerUrl;

}
