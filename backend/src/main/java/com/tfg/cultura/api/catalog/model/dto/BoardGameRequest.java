package com.tfg.cultura.api.catalog.model.dto;

import com.tfg.cultura.api.catalog.model.enumerators.BoardGameType;
import com.tfg.cultura.api.catalog.model.enumerators.Complexity;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
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
public class BoardGameRequest extends ItemRequest {
	@Min(value = 1, message = "El número mínimo de jugadores debe ser al menos 1")
	@NotNull(message = "El número mínimo de jugadores es obligatorio")
	private Integer minPlayers;

	@Min(value = 1, message = "El número máximo de jugadores debe ser al menos 1")
	@NotNull(message = "El número máximo de jugadores es obligatorio")
	private Integer maxPlayers;

	@Min(value = 1, message = "El tiempo de juego debe ser al menos 1 minuto")
	@NotNull(message = "El tiempo de juego es obligatorio")
	private Integer playTime; // Tiempo de juego en minutos

	@NotNull(message = "La complejidad del juego es obligatoria")
	private Complexity complexity;

	@NotNull(message = "El tipo de juego es obligatorio")
	@NotEmpty(message = "Debe haber al menos un tipo de juego")
	private BoardGameType[] types;

	private String baseGameId; // Referencia a otro juego de mesa si es una expansión

}
