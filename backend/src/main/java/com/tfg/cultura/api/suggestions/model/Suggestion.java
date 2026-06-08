package com.tfg.cultura.api.suggestions.model;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tfg.cultura.api.suggestions.model.enumerators.SuggestionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Document(collection = "suggestions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Suggestion {
    @Id
    private String id;

    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 50, message = "El título debe tener entre 3 y 50 caracteres")
    private String title;

    @Size(min = 0, max = 280, message = "La descripción no puede tener más de 280 caracteres")
    private String description;

    @Builder.Default
    @NotNull(message = "El tipo de sugerencia es obligatorio")
    private SuggestionType type = SuggestionType.OTHER;

    @NotBlank(message = "El autor es obligatorio")
    private String authorId;

    @Builder.Default
    @NotNull
    private List<String> supportersId = new ArrayList<>();

    @Builder.Default
    @NotNull
    private int totalSupporters = 0;

    @CreatedDate
    private LocalDateTime createdAt;

}
