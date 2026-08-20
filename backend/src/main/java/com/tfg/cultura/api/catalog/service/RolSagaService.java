package com.tfg.cultura.api.catalog.service;

import java.util.Set;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.catalog.exception.category.CategoryNotFoundException;
import com.tfg.cultura.api.catalog.exception.rolsaga.RolSagaAlreadyExistsException;
import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.model.RolSaga;
import com.tfg.cultura.api.catalog.model.dto.RolSagaRequest;
import com.tfg.cultura.api.catalog.model.dto.RolSagaResponse;
import com.tfg.cultura.api.catalog.repository.RolSagaRepository;
import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.exception.SectionNotFoundException;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.service.SectionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class RolSagaService {

    private final RolSagaRepository repository;
    private final SectionService sectionService;
    private final CategoryService categoryService;
    private final FileService fileService;
    private final AppProperties appProperties;

    private String getImageFolder() {
        return "rolsaga";
    }

    private String getDefaultImageUrl() {
        return appProperties.defaultImages().rolSaga();
    }

    // CREATE

    public RolSagaResponse create(RolSagaRequest request, MultipartFile image)
            throws CategoryNotFoundException, SectionNotFoundException, RolSagaAlreadyExistsException {
        boolean exists = repository.existsByName(request.getName());
        if (exists) {
            throw new RolSagaAlreadyExistsException(request.getName());
        }

        Set<Category> categories = categoryService.findCategoriesByIds(request.getCategoriesIds());
        Section section = sectionService.findSectionById(request.getSectionId());

        RolSaga rolSaga = new RolSaga(request, section, categories);
        RolSaga savedRolSaga = repository.save(rolSaga);

        setImage(savedRolSaga, image);

        return new RolSagaResponse(savedRolSaga);
    }

    private void setImage(RolSaga rolSaga, MultipartFile image) {
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

}
