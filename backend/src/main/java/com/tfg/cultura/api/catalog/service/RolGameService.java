package com.tfg.cultura.api.catalog.service;

import com.tfg.cultura.api.catalog.exception.rolsaga.RolSagaNotFoundException;
import com.tfg.cultura.api.catalog.model.RolGame;
import com.tfg.cultura.api.catalog.model.RolSaga;
import com.tfg.cultura.api.catalog.model.dto.RolGameRequest;
import com.tfg.cultura.api.catalog.model.dto.RolGameResponse;
import com.tfg.cultura.api.catalog.repository.RolGameRepository;
import com.tfg.cultura.api.categories.service.CategoryService;
import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.service.SectionService;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class RolGameService extends AbstractItemService<RolGame, RolGameRepository, RolGameRequest, RolGameResponse> {

	private final AppProperties appProperties;
	private final RolSagaService rolSagaService;

	public RolGameService(RolGameRepository rolGameRepository, SectionService sectionService,
			CategoryService categoryService, FileService fileService, AppProperties appProperties,
			RolSagaService rolSagaService) {
		super(rolGameRepository, sectionService, categoryService, fileService, RolGameResponse::new);
		this.appProperties = appProperties;
		this.rolSagaService = rolSagaService;
	}

	@Override
	protected String getImageFolder() {
		return "cultura/items/rolgames";
	}

	@Override
	protected String getDefaultImageUrl() {
		return appProperties.defaultImages().rolGame();
	}

	@Override
	protected void validate(RolGame item) {
		// No additional validation needed for RolGame
	}

	@Override
	protected RolGame createEntity() {
		return RolGame.builder().build();
	}

	@Override
	protected void fillSpecificFields(RolGame item, RolGameRequest request) throws RolSagaNotFoundException {
		RolSaga saga = rolSagaService.findById(request.getSagaId());
		item.setSaga(saga);
		item.setType(request.getType());

		item.setCategories(saga.getCategories());
		item.setSection(saga.getSection());
	}

	@Override
	protected Integer getLoanDays(RolGameRequest request) {
		return 15; // RN-17
	}

	public List<RolGameResponse> findAllBySagaId(String sagaId) throws RolSagaNotFoundException {
		RolSaga saga = rolSagaService.findById(sagaId);
		List<RolGame> rolGames = repository.findAllBySaga(saga);
		return rolGames.stream().map(RolGameResponse::new).toList();
	}

}
