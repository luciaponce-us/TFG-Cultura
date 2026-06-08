package com.tfg.cultura.api.suggestions.model.dto;

import com.tfg.cultura.api.suggestions.model.enumerators.SuggestionType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SuggestionCreateRequest {
    
    @NotBlank(message = "El título es obligatorio")
    @Size(min = 3, max = 50, message = "El título debe tener entre 3 y 50 caracteres")
    private String title;

    @Size(min = 0, max = 280, message = "La descripción no puede tener más de 280 caracteres")
    private String description;

    @Builder.Default
    @NotNull(message = "El tipo de sugerencia es obligatorio")
    private SuggestionType type = SuggestionType.OTHER;
    
}
