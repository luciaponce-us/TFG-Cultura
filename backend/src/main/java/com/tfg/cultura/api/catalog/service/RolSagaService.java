package com.tfg.cultura.api.catalog.service;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.catalog.exception.category.CategoryNotFoundException;
import com.tfg.cultura.api.catalog.exception.rolsaga.RolSagaAlreadyExistsException;
import com.tfg.cultura.api.catalog.exception.rolsaga.RolSagaNotFoundException;
import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.model.RolSaga;
import com.tfg.cultura.api.catalog.model.dto.RolSagaRequest;
import com.tfg.cultura.api.catalog.model.dto.RolSagaResponse;
import com.tfg.cultura.api.catalog.repository.RolGameRepository;
import com.tfg.cultura.api.catalog.repository.RolSagaRepository;
import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.exception.FileDeleteException;
import com.tfg.cultura.api.core.exception.FileUploadException;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.exception.SectionNotFoundException;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.service.SectionService;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolSagaService {

    private final RolSagaRepository repository;
    private final SectionService sectionService;
    private final CategoryService categoryService;
    private final FileService fileService;
    private final RolGameRepository rolGameRepository;
    private final AppProperties appProperties;

    private String getImageFolder() {
        return "cultura/items/rolsaga";
    }

    private String getDefaultImageUrl() {
        return appProperties.defaultImages().rolSaga();
    }

    // CREATE

    @Transactional
    public RolSagaResponse create(RolSagaRequest request, MultipartFile image)
            throws CategoryNotFoundException, SectionNotFoundException, RolSagaAlreadyExistsException,
            FileDeleteException, FileUploadException {

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

        String imageUrl = fileService.uploadImage(
                "rolsaga",
                rolSaga.getId(),
                image,
                getImageFolder(),
                getDefaultImageUrl(),
                400,
                600);

        rolSaga.setImageUrl(imageUrl);
    }

    // READ

    public RolSagaResponse getById(String id) throws RolSagaNotFoundException {
        RolSaga rolSaga = findById(id);
        return new RolSagaResponse(rolSaga);
    }

    protected RolSaga findById(String id) throws RolSagaNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> new RolSagaNotFoundException(id));
    }

    public Page<RolSagaResponse> getAll(Pageable pageable) {
        return repository.findAll(pageable)
                .map(RolSagaResponse::new);
    }

    // UPDATE

    @Transactional
    public RolSagaResponse update(String id, RolSagaRequest request, MultipartFile image)
            throws CategoryNotFoundException, SectionNotFoundException, RolSagaNotFoundException,
            RolSagaAlreadyExistsException, FileDeleteException, FileUploadException {
        RolSaga existingRolSaga = findById(id);
        boolean nameChanged = !existingRolSaga.getName().toLowerCase().equals(request.getName().trim().toLowerCase());
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
    public void delete(String id) throws RolSagaNotFoundException, FileDeleteException {
        RolSaga rolSaga = findById(id);
        //deleteImage(rolSaga.getImageUrl());
        rolGameRepository.deleteAllBySaga(rolSaga);
        repository.delete(rolSaga);
    }

    private void deleteImage(String imageUrl) throws FileDeleteException {
        if (imageUrl != null && !imageUrl.equals(getDefaultImageUrl())) {
            fileService.deleteFile(imageUrl);
        }
    }

    private void checkNameUniqueness(String name, String id) throws RolSagaAlreadyExistsException {
        if (repository.existsByNameAndIdNot(name.trim(), id)) {
            throw new RolSagaAlreadyExistsException(name.trim());
        }
    }

}
