package com.tfg.cultura.api.catalog.service;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

import com.tfg.cultura.api.catalog.model.RolSaga;
import com.tfg.cultura.api.catalog.model.dto.RolSagaRequest;
import com.tfg.cultura.api.catalog.model.dto.RolSagaResponse;
import com.tfg.cultura.api.catalog.repository.RolGameRepository;
import com.tfg.cultura.api.catalog.repository.RolSagaRepository;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.categories.service.CategoryService;
import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.exception.DuplicationException;
import com.tfg.cultura.api.core.exception.NotFoundException;
import com.tfg.cultura.api.core.exception.file.FileDeleteException;
import com.tfg.cultura.api.core.exception.file.FileUploadException;
import com.tfg.cultura.api.core.model.dto.FileUploadRequest;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.service.SectionService;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class RolSagaService {

	private final RolSagaRepository repository;
	private final SectionService sectionService;
	private final CategoryService categoryService;
	private final FileService fileService;
	private final RolGameRepository rolGameRepository;
	private final AppProperties appProperties;
	private final Logger logger = LoggerFactory.getLogger("catalogLogger");

	private static final String FOLDER = "cultura/items/rolsaga";

	private String getDefaultImageUrl() {
		return appProperties.defaultImages().rolSaga();
	}

	// CREATE

	@Transactional
	public RolSagaResponse create(RolSagaRequest request, MultipartFile image)
			throws NotFoundException, DuplicationException, FileDeleteException, FileUploadException {

		checkNameUniqueness(request.getName().trim(), null);

		Set<Category> categories = categoryService.findCategoriesByIds(request.getCategoriesIds());
		Section section = sectionService.findSectionById(request.getSectionId());

		RolSaga rolSaga = new RolSaga(request, section, categories);
		RolSaga savedRolSaga = repository.save(rolSaga);

		setImage(savedRolSaga, image);

		return new RolSagaResponse(savedRolSaga);
	}

	private void setImage(RolSaga rolSaga, MultipartFile image) throws FileDeleteException, FileUploadException {
		if (image == null || image.isEmpty()) {
			return;
		}

		deleteImage(rolSaga.getImageUrl());

		FileUploadRequest fileUploadRequest = FileUploadRequest.builder().file(image).folder(FOLDER)
				.className("rolsaga").id(rolSaga.getId()).width(400).height(600).defaultFileUrl(getDefaultImageUrl())
				.resourceType("image").field("imageUrl").build();

		String imageUrl = fileService.uploadImage(fileUploadRequest, logger);

		rolSaga.setImageUrl(imageUrl);
	}

	// READ

	public RolSagaResponse getById(String id) throws NotFoundException {
		RolSaga rolSaga = findById(id);
		return new RolSagaResponse(rolSaga);
	}

	protected RolSaga findById(String id) throws NotFoundException {
		return repository.findById(id).orElseThrow(
				() -> new NotFoundException("Saga de rol con id " + sanitize(id) + " no encontrada", logger));
	}

	public Page<RolSagaResponse> getAll(Pageable pageable) {
		return repository.findAll(pageable).map(RolSagaResponse::new);
	}

	// UPDATE

	@Transactional
	public RolSagaResponse update(String id, RolSagaRequest request, MultipartFile image)
			throws NotFoundException, DuplicationException, FileDeleteException, FileUploadException {
		RolSaga existingRolSaga = findById(id);
		boolean nameChanged = !existingRolSaga.getName().equalsIgnoreCase(request.getName().trim());
		if (nameChanged) {
			checkNameUniqueness(request.getName().trim(), id);
		}

		Set<Category> categories = categoryService.findCategoriesByIds(request.getCategoriesIds());
		Section section = sectionService.findSectionById(request.getSectionId());

		existingRolSaga.setName(request.getName().trim());
		existingRolSaga.setDescription(request.getDescription().trim());
		existingRolSaga.setWebsite(sanitize(request.getWebsite()));
		existingRolSaga.setCharacterSheetUrl(sanitize(request.getCharacterSheetUrl()));
		existingRolSaga.setGameMaster(request.getGameMaster());
		existingRolSaga.setDice(sanitize(request.getDice()));
		existingRolSaga.setRecommendedPlayers(sanitize(request.getRecommendedPlayers()));
		existingRolSaga.setSection(section);
		existingRolSaga.setCategories(categories);

		if (image != null && !image.isEmpty()) {
			setImage(existingRolSaga, image);
		}

		RolSaga updatedRolSaga = repository.save(existingRolSaga);
		return new RolSagaResponse(updatedRolSaga);
	}

	// DELETE

	@Transactional
	public void delete(String id) throws NotFoundException, FileDeleteException {
		RolSaga rolSaga = findById(id);
		deleteImage(rolSaga.getImageUrl());
		rolGameRepository.deleteAllBySaga(rolSaga);
		repository.delete(rolSaga);
	}

	private void deleteImage(String imageUrl) throws FileDeleteException {
		if (imageUrl != null && !imageUrl.equals(getDefaultImageUrl())) {
			fileService.deleteFile(imageUrl);
		}
	}

	private void checkNameUniqueness(String name, String id) throws DuplicationException {
		if (repository.existsByNameAndIdNot(name.trim(), id)) {
			throw new DuplicationException(logger, Map.of("name", "Ya existe una saga de rol con el mismo nombre"));
		}
	}

}
