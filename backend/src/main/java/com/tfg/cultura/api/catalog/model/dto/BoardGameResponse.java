package com.tfg.cultura.api.catalog.model.dto;

import com.tfg.cultura.api.catalog.model.BoardGame;
import com.tfg.cultura.api.catalog.model.enumerators.BoardGameType;
import com.tfg.cultura.api.catalog.model.enumerators.Complexity;
import java.util.Set;
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
public class BoardGameResponse extends ItemResponse {

	private Integer minPlayers;
	private Integer maxPlayers;
	private Integer playTime; // Tiempo de juego en minutos
	private Complexity complexity;
	private Set<BoardGameType> types;
	private BoardGame baseGame; // Referencia a otro juego de mesa si es una expansión
	private Boolean isExpansion; // Indica si el juego es una expansión de otro juego

	public BoardGameResponse(BoardGame boardGame) {
		super(boardGame);

		this.minPlayers = boardGame.getMinPlayers();
		this.maxPlayers = boardGame.getMaxPlayers();
		this.playTime = boardGame.getPlayTime();
		this.complexity = boardGame.getComplexity();
		this.types = boardGame.getTypes();
		this.baseGame = boardGame.getBaseGame();
		this.isExpansion = boardGame.getBaseGame() != null;
	}

}
