package com.tfg.cultura.api.catalog.model.dto;

import java.util.Set;

import com.tfg.cultura.api.catalog.model.enumerators.GameMaster;
import com.tfg.cultura.api.core.validation.annotations.ValidUrl;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RolSagaRequest {

    @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
    @NotBlank(message = "El nombre es obligatorio")
    private String name;

    @Size(max = 280, message = "La descripción no puede tener más de 280 caracteres")
    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @Size(max = 280, message = "La URL del sitio web no puede tener más de 280 caracteres")
    @ValidUrl(message = "La URL del sitio web no es válida")
    private String website;

    @Size(max = 280, message = "La URL de la hoja de personaje no puede tener más de 280 caracteres")
    @ValidUrl(message = "La URL de la hoja de personaje no es válida")
    private String characterSheetUrl;

    @NotNull(message = "El maestro del juego es obligatorio")
    private GameMaster gameMaster;

    @Size(max = 100, message = "Los dados no pueden tener más de 100 caracteres")
    private String dice; // Dados utilizados en la saga (por ejemplo, D20, D6, etc.)

    @Size(max = 50, message = "Los jugadores recomendados no pueden tener más de 50 caracteres")
    private String recommendedPlayers;

    @NotNull(message = "La saga debe pertenecer a una sección")
    private String sectionId;

    private Set<String> categoriesIds;
    
}
