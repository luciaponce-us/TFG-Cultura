package com.tfg.cultura.api.catalog.model;

import java.time.LocalDate;

import org.springframework.data.mongodb.core.mapping.Document;

import com.tfg.cultura.api.catalog.model.enumerators.Platform;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

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
    private String trailerUrl;
    
}
