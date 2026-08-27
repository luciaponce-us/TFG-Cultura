package com.tfg.cultura.api.catalog.model.dto;

import com.tfg.cultura.api.catalog.model.RolGame;
import com.tfg.cultura.api.catalog.model.enumerators.RolBookType;

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
public class RolGameResponse extends ItemResponse {
	private RolSagaReference saga;
	private RolBookType type;

	public RolGameResponse(RolGame rolGame) {
		super(rolGame);
		this.saga = new RolSagaReference(rolGame.getSaga());
		this.type = rolGame.getType();
	}

}
