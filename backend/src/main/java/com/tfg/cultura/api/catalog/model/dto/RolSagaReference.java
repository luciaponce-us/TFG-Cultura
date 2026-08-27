package com.tfg.cultura.api.catalog.model.dto;

import com.tfg.cultura.api.catalog.model.RolSaga;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RolSagaReference {
	private String id;
	private String imageUrl;
	private String name;

	public RolSagaReference(RolSaga rolSaga) {
		this.id = rolSaga.getId();
		this.imageUrl = rolSaga.getImageUrl();
		this.name = rolSaga.getName();
	}
}
