package com.tfg.cultura.api.catalog.model;

import java.util.List;
import java.util.ArrayList;

import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Document(collection = "rolgames")
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
public class RolGame extends Item {

    @DocumentReference
    @NotNull(message = "La saga es obligatoria")
    private RolSaga saga;

    @DocumentReference
    @NotNull(message = "La lista de expansiones es obligatoria")
    @Builder.Default
    private List<RolGame> expansions = new ArrayList<RolGame>();
}
