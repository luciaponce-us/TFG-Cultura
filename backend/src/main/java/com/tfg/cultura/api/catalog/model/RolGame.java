package com.tfg.cultura.api.catalog.model;

import com.tfg.cultura.api.catalog.model.enumerators.RolBookType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.DocumentReference;

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

	@NotNull(message = "El tipo de libro es obligatorio")
	@Builder.Default
	private RolBookType type = RolBookType.BASIC;
}
