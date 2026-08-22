package com.tfg.cultura.api.catalog.model.dto;

import com.tfg.cultura.api.catalog.model.enumerators.RolBookType;

import jakarta.validation.constraints.NotNull;
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
public class RolGameRequest extends ItemRequest {
    @NotNull(message = "La saga es obligatoria")
    private String sagaId;

    @NotNull(message = "El tipo de libro es obligatorio")
    private RolBookType type;
    
}
