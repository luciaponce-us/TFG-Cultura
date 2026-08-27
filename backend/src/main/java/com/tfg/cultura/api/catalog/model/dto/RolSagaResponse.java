package com.tfg.cultura.api.catalog.model.dto;

import com.tfg.cultura.api.catalog.model.RolSaga;
import com.tfg.cultura.api.catalog.model.enumerators.GameMaster;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.sections.model.dto.SectionReference;
import java.time.LocalDateTime;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class RolSagaResponse {
	private String id;
	private String imageUrl;
	private String name;
	private String description;
	private String website;
	private String characterSheetUrl;
	private GameMaster gameMaster;
	private String dice; // Dados utilizados en la saga (por ejemplo, D20, D6, etc.)
	private String recommendedPlayers;
	private SectionReference section;
	private Set<Category> categories;
	private LocalDateTime createdAt;

	public RolSagaResponse(RolSaga rolSaga) {
		this.id = rolSaga.getId();
		this.imageUrl = rolSaga.getImageUrl();
		this.name = rolSaga.getName();
		this.description = rolSaga.getDescription();
		this.website = rolSaga.getWebsite();
		this.characterSheetUrl = rolSaga.getCharacterSheetUrl();
		this.gameMaster = rolSaga.getGameMaster();
		this.dice = rolSaga.getDice();
		this.recommendedPlayers = rolSaga.getRecommendedPlayers();
		this.section = new SectionReference(rolSaga.getSection());
		this.categories = rolSaga.getCategories();
		this.createdAt = rolSaga.getCreatedAt();
	}
}
