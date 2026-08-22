package com.tfg.cultura.api.catalog.model;

import java.time.LocalDateTime;

import java.util.Set;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import com.tfg.cultura.api.catalog.model.dto.RolSagaRequest;
import com.tfg.cultura.api.catalog.model.enumerators.GameMaster;
import com.tfg.cultura.api.core.validation.annotations.ValidCloudinaryUrl;
import com.tfg.cultura.api.core.validation.enums.ResourceType;
import com.tfg.cultura.api.sections.model.Section;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

@Document(collection = "rolsagas")
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RolSaga {

    private static final String DEFAULT_IMAGE_URL = "https://res.cloudinary.com/dubz79y98/image/upload/v1787070899/boardgame_placeholder.jpg";

    @Id
    private String id;

    @ValidCloudinaryUrl(type = ResourceType.IMAGE, message = "La URL de la imagen no es válida")
    @Builder.Default
    private String imageUrl = DEFAULT_IMAGE_URL;

    @Size(min = 3, max = 120, message = "El nombre debe tener entre 3 y 120 caracteres")
    @NotBlank(message = "El nombre es obligatorio")
    @Indexed(unique = true)
    private String name;

    @Size(max = 280, message = "La descripción no puede tener más de 280 caracteres")
    @NotBlank(message = "La descripción es obligatoria")
    private String description;

    @Size(max = 280, message = "La URL del sitio web no puede tener más de 280 caracteres")
    private String website;

    @Size(max = 280, message = "La URL de la hoja de personaje no puede tener más de 280 caracteres")
    private String characterSheetUrl;

    @NotNull(message = "El maestro del juego es obligatorio")
    private GameMaster gameMaster;

    @Size(max = 100, message = "Los dados no pueden tener más de 100 caracteres")
    private String dice; // Dados utilizados en la saga (por ejemplo, D20, D6, etc.)

    @Size(max = 50, message = "Los jugadores recomendados no pueden tener más de 50 caracteres")
    private String recommendedPlayers;

    @NotNull(message = "La saga debe pertenecer a una sección")
    @DocumentReference
    private Section section;

    @DocumentReference
    private Set<Category> categories;

    @CreatedDate
    private LocalDateTime createdAt;

    public RolSaga(RolSagaRequest request, Section section, Set<Category> categories) {
        this.name = request.getName().trim();
        this.description = request.getDescription().trim();
        this.website = sanitize(request.getWebsite());
        this.characterSheetUrl = sanitize(request.getCharacterSheetUrl());
        this.gameMaster = request.getGameMaster();
        this.dice = sanitize(request.getDice());
        this.recommendedPlayers = sanitize(request.getRecommendedPlayers());
        this.section = section;
        this.categories = categories;
    }
}
