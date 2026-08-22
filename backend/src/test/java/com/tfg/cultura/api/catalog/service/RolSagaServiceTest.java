package com.tfg.cultura.api.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.mock.web.MockMultipartFile;

import com.tfg.cultura.api.catalog.exception.category.CategoryNotFoundException;
import com.tfg.cultura.api.catalog.exception.rolsaga.RolSagaAlreadyExistsException;
import com.tfg.cultura.api.catalog.exception.rolsaga.RolSagaNotFoundException;
import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.RolSaga;
import com.tfg.cultura.api.catalog.model.dto.RolSagaRequest;
import com.tfg.cultura.api.catalog.model.dto.RolSagaResponse;
import com.tfg.cultura.api.catalog.repository.RolGameRepository;
import com.tfg.cultura.api.catalog.repository.RolSagaRepository;
import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.exception.FileDeleteException;
import com.tfg.cultura.api.core.exception.FileUploadException;
import com.tfg.cultura.api.core.factory.FileFactory;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.exception.SectionNotFoundException;
import com.tfg.cultura.api.sections.service.SectionService;

@ExtendWith(MockitoExtension.class)
class RolSagaServiceTest {
    @Mock
    private RolSagaRepository repository;

    @Mock
    private RolGameRepository rolGameRepository;
    
    @Mock
    private SectionService sectionService;

    @Mock
    private CategoryService categoryService;
    
    @Mock
    private FileService fileService;

    @Mock
    private AppProperties appProperties;

    @Mock
    private AppProperties.DefaultImages defaultImages;

    @InjectMocks
    private RolSagaService service;

    private RolSaga rolSaga;
    private RolSagaRequest request;
    private MockMultipartFile image = FileFactory.mockImagePart();
    private MockMultipartFile emptyImage = FileFactory.mockEmptyImagePart();

    @BeforeEach
    void setUp() {
        rolSaga = CatalogFactory.validRolSaga();
        request = CatalogFactory.validRolSagaRequest();
    }

    // CREATE

    @Test
    void should_create_rol_saga_successfully_without_image() {
        // Given
        when(repository.existsByNameAndIdNot(request.getName(), null)).thenReturn(false);
        when(sectionService.findSectionById(request.getSectionId()))
                .thenReturn(rolSaga.getSection());

        when(repository.save(any(RolSaga.class))).thenReturn(rolSaga);

        // When
        RolSagaResponse response = service.create(request, null);

        // Then
        assertNotNull(response);

        verify(repository).existsByNameAndIdNot(request.getName(), null);
        verify(categoryService).findCategoriesByIds(request.getCategoriesIds());
        verify(sectionService).findSectionById(request.getSectionId());
        verify(repository).save(any(RolSaga.class));

        verifyNoInteractions(fileService);
    }

    @Test
    void should_create_rol_saga_successfully_with_image()
            throws FileUploadException, FileDeleteException {

        // Given

        when(repository.existsByNameAndIdNot(request.getName(), null)).thenReturn(false);
        when(sectionService.findSectionById(request.getSectionId()))
                .thenReturn(rolSaga.getSection());

        when(repository.save(any(RolSaga.class))).thenReturn(rolSaga);

        when(fileService.uploadImage(
                eq("rolsaga"),
                eq("rol-saga-id"),
                eq(image),
                anyString(),
                anyString(),
                anyInt(),
                anyInt()))
                .thenReturn("new-image-url");

        when(appProperties.defaultImages()).thenReturn(defaultImages);
        when(defaultImages.rolSaga()).thenReturn("https://example.com/default-rol-saga.png");

        // When
        RolSagaResponse response = service.create(request, image);

        // Then
        assertNotNull(response);
        assertEquals("new-image-url", rolSaga.getImageUrl());

        verify(repository).save(any(RolSaga.class));

        verify(fileService).uploadImage(
                eq("rolsaga"),
                eq("rol-saga-id"),
                eq(image),
                anyString(),
                anyString(),
                anyInt(),
                anyInt());
    }

    @Test
    void should_not_upload_image_when_image_is_empty()
            throws FileUploadException, FileDeleteException {

        // Given
        when(repository.existsByNameAndIdNot(request.getName(), null)).thenReturn(false);
        when(sectionService.findSectionById(request.getSectionId()))
                .thenReturn(rolSaga.getSection());

        when(repository.save(any(RolSaga.class))).thenReturn(rolSaga);

        // When
        RolSagaResponse response = service.create(request, emptyImage);

        // Then
        assertNotNull(response);

        verify(repository).save(any(RolSaga.class));

        verifyNoInteractions(fileService);
    }

    @Test
    void should_throw_exception_when_rol_saga_already_exists() {

        // Given
        when(repository.existsByNameAndIdNot(request.getName(), null)).thenReturn(true);

        // When & Then
        assertThrows(
                RolSagaAlreadyExistsException.class,
                () -> service.create(request, null));

        verify(repository).existsByNameAndIdNot(request.getName(), null);

        verify(repository, never()).save(any());
        verifyNoInteractions(categoryService);
        verifyNoInteractions(sectionService);
        verifyNoInteractions(fileService);
    }

    @Test
    void should_propagate_category_not_found_exception() {

        // Given
        when(repository.existsByNameAndIdNot(request.getName(), null)).thenReturn(false);

        when(categoryService.findCategoriesByIds(request.getCategoriesIds()))
                .thenThrow(new CategoryNotFoundException("category-id"));

        // When & Then
        assertThrows(
                CategoryNotFoundException.class,
                () -> service.create(request, null));

        verify(categoryService).findCategoriesByIds(request.getCategoriesIds());

        verify(sectionService, never()).findSectionById(any());
        verify(repository, never()).save(any());
    }

    @Test
    void should_propagate_section_not_found_exception() {

        // Given
        when(repository.existsByNameAndIdNot(request.getName(), null)).thenReturn(false);

        when(sectionService.findSectionById(request.getSectionId()))
                .thenThrow(new SectionNotFoundException(request.getSectionId()));

        // When & Then
        assertThrows(
                SectionNotFoundException.class,
                () -> service.create(request, null));

        verify(repository, never()).save(any());
    }

    @Test
    void should_propagate_file_upload_exception()
            throws FileDeleteException, FileUploadException {

        // Given

        when(repository.existsByNameAndIdNot(request.getName(), null)).thenReturn(false);
        when(sectionService.findSectionById(request.getSectionId()))
                .thenReturn(rolSaga.getSection());

        when(repository.save(any(RolSaga.class))).thenReturn(rolSaga);
        when(appProperties.defaultImages()).thenReturn(defaultImages);
        when(defaultImages.rolSaga()).thenReturn("https://example.com/default-rol-saga.png");

        doThrow(new FileUploadException("Error uploading image"))
                .when(fileService)
                .uploadImage(
                        eq("rolsaga"),
                        eq(rolSaga.getId()),
                        eq(image),
                        anyString(),
                        anyString(),
                        anyInt(),
                        anyInt());

        // When & Then
        assertThrows(
                FileUploadException.class,
                () -> service.create(request, image));

        verify(repository).save(any(RolSaga.class));
    }

    // READ

    @Test
    void should_get_rol_saga_by_id() throws RolSagaNotFoundException {
        // Given

        when(repository.findById(rolSaga.getId())).thenReturn(Optional.of(rolSaga));

        // When
        RolSagaResponse response = service.getById(rolSaga.getId());

        // Then
        assertNotNull(response);
        assertEquals(rolSaga.getId(), response.getId());
        assertEquals(rolSaga.getName(), response.getName());

        verify(repository).findById(rolSaga.getId());
    }

    @Test
    void should_throw_exception_when_rol_saga_does_not_exist() {

        // Given
        String id = "non-existent-id";

        when(repository.findById(id)).thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                RolSagaNotFoundException.class,
                () -> service.getById(id));

        verify(repository).findById(id);
    }

    @Test
    void should_get_all_rol_sagas() {

        // Given
        PageRequest pageable = PageRequest.of(0, 10);

        Page<RolSaga> page = new PageImpl<>(
                List.of(rolSaga),
                pageable,
                1);

        when(repository.findAll(pageable)).thenReturn(page);

        // When
        Page<RolSagaResponse> response = service.getAll(pageable);

        // Then
        assertNotNull(response);
        assertEquals(1, response.getTotalElements());
        assertEquals(1, response.getContent().size());

        assertEquals(rolSaga.getId(), response.getContent().get(0).getId());
        assertEquals(rolSaga.getName(), response.getContent().get(0).getName());

        verify(repository).findAll(pageable);
    }

    // UPDATE

    @Test
    void should_update_rol_saga_without_changing_name_or_image()
            throws CategoryNotFoundException, SectionNotFoundException,
            RolSagaNotFoundException, RolSagaAlreadyExistsException,
            FileDeleteException, FileUploadException {

        // Given

        when(repository.findById(rolSaga.getId()))
                .thenReturn(Optional.of(rolSaga));

        when(sectionService.findSectionById(request.getSectionId()))
                .thenReturn(rolSaga.getSection());

        when(repository.save(rolSaga))
                .thenReturn(rolSaga);

        // When
        RolSagaResponse response = service.update(rolSaga.getId(), request, null);

        // Then
        assertNotNull(response);

        assertEquals(request.getName().trim(), rolSaga.getName());
        assertEquals(request.getDescription().trim(), rolSaga.getDescription());
        assertEquals(request.getGameMaster(), rolSaga.getGameMaster());

        assertEquals(rolSaga.getSection(), rolSaga.getSection());
        assertEquals(rolSaga.getCategories(), rolSaga.getCategories());

        verify(repository).findById(rolSaga.getId());
        verify(categoryService).findCategoriesByIds(request.getCategoriesIds());
        verify(sectionService).findSectionById(request.getSectionId());
        verify(repository).save(rolSaga);

        verifyNoInteractions(fileService);
    }

    @Test
    void should_update_rol_saga_when_name_changes()
            throws CategoryNotFoundException, SectionNotFoundException,
            RolSagaNotFoundException, RolSagaAlreadyExistsException,
            FileDeleteException, FileUploadException {

        // Given

        when(repository.findById(rolSaga.getId()))
                .thenReturn(Optional.of(rolSaga));

        when(repository.existsByNameAndIdNot(request.getName().trim(), rolSaga.getId()))
                .thenReturn(false);

        when(sectionService.findSectionById(request.getSectionId()))
                .thenReturn(rolSaga.getSection());

        when(repository.save(rolSaga))
                .thenReturn(rolSaga);

        // When
        RolSagaResponse response = service.update(rolSaga.getId(), request, null);

        // Then
        assertNotNull(response);
        assertEquals(request.getName().trim(), rolSaga.getName());

        verify(repository).findById(rolSaga.getId());
        verify(repository).existsByNameAndIdNot(request.getName().trim(), rolSaga.getId());
        verify(repository).save(rolSaga);

        verifyNoInteractions(fileService);
    }

    @Test
    void should_throw_exception_when_updating_non_existing_rol_saga() {

        // Given
        String id = "non-existent-id";

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                RolSagaNotFoundException.class,
                () -> service.update(id, request, null));

        verify(repository).findById(id);

        verify(repository, never()).save(any());
        verifyNoInteractions(categoryService);
        verifyNoInteractions(sectionService);
        verifyNoInteractions(fileService);
    }

    @Test
    void should_throw_exception_when_new_name_already_exists() {

        // Given
        request.setName(" Forgotten Realms ");

        when(repository.findById(rolSaga.getId()))
                .thenReturn(Optional.of(rolSaga));

        when(repository.existsByNameAndIdNot("Forgotten Realms", rolSaga.getId()))
                .thenReturn(true);

        // When & Then
        assertThrows(
                RolSagaAlreadyExistsException.class,
                () -> service.update(rolSaga.getId(), request, null));

        verify(repository).findById(rolSaga.getId());
        verify(repository).existsByNameAndIdNot("Forgotten Realms", rolSaga.getId());

        verify(repository, never()).save(any());
        verifyNoInteractions(categoryService);
        verifyNoInteractions(sectionService);
    }

    @Test
    void should_propagate_category_not_found_exception_when_updating()
            throws CategoryNotFoundException {

        // Given

        request.setCategoriesIds(Set.of("category-id"));

        when(repository.findById(rolSaga.getId()))
                .thenReturn(Optional.of(rolSaga));

        when(categoryService.findCategoriesByIds(request.getCategoriesIds()))
                .thenThrow(new CategoryNotFoundException("category-id"));

        // When & Then
        assertThrows(
                CategoryNotFoundException.class,
                () -> service.update(rolSaga.getId(), request, null));

        verify(repository).findById(rolSaga.getId());
        verify(categoryService).findCategoriesByIds(request.getCategoriesIds());

        verify(sectionService, never()).findSectionById(any());
        verify(repository, never()).save(any());
    }

    @Test
    void should_propagate_section_not_found_exception_when_updating()
            throws CategoryNotFoundException, SectionNotFoundException {

        // Given
        when(repository.findById(rolSaga.getId()))
                .thenReturn(Optional.of(rolSaga));

        when(sectionService.findSectionById(request.getSectionId()))
                .thenThrow(new SectionNotFoundException(request.getSectionId()));

        // When & Then
        assertThrows(
                SectionNotFoundException.class,
                () -> service.update(rolSaga.getId(), request, null));

        verify(repository).findById(rolSaga.getId());
        verify(categoryService).findCategoriesByIds(request.getCategoriesIds());
        verify(sectionService).findSectionById(request.getSectionId());

        verify(repository, never()).save(any());
    }

    @Test
    void should_update_rol_saga_with_image()
            throws CategoryNotFoundException, SectionNotFoundException,
            RolSagaNotFoundException, RolSagaAlreadyExistsException,
            FileDeleteException, FileUploadException {

        // Given

        when(repository.findById(rolSaga.getId()))
                .thenReturn(Optional.of(rolSaga));

        when(sectionService.findSectionById(request.getSectionId()))
                .thenReturn(rolSaga.getSection());

        when(fileService.uploadImage(
                eq("rolsaga"),
                eq(rolSaga.getId()),
                eq(image),
                anyString(),
                anyString(),
                anyInt(),
                anyInt()))
                .thenReturn("new-image-url");

        when(repository.save(rolSaga))
                .thenReturn(rolSaga);

        when(appProperties.defaultImages()).thenReturn(defaultImages);
        when(defaultImages.rolSaga()).thenReturn("https://example.com/default-rol-saga.png");

        // When
        RolSagaResponse response = service.update(rolSaga.getId(), request, image);

        // Then
        assertNotNull(response);
        assertEquals("new-image-url", rolSaga.getImageUrl());

        verify(fileService).deleteFile("old-image-url");

        verify(fileService).uploadImage(any(), anyString(), any(), anyString(), anyString(), anyInt(), anyInt());

        verify(repository).save(rolSaga);
    }

    @Test
    void should_not_update_image_when_image_is_empty()
            throws CategoryNotFoundException, SectionNotFoundException,
            RolSagaNotFoundException, RolSagaAlreadyExistsException,
            FileDeleteException, FileUploadException {

        // Given
        when(repository.findById(rolSaga.getId()))
                .thenReturn(Optional.of(rolSaga));

        when(sectionService.findSectionById(request.getSectionId()))
                .thenReturn(rolSaga.getSection());

        when(repository.save(rolSaga))
                .thenReturn(rolSaga);

        // When
        RolSagaResponse response = service.update(rolSaga.getId(), request, emptyImage);

        // Then
        assertNotNull(response);
        assertEquals("old-image-url", rolSaga.getImageUrl());

        verifyNoInteractions(fileService);

        verify(repository).save(rolSaga);
    }

    // DELETE

    @Test
    void should_delete_rol_saga_and_its_image()
            throws RolSagaNotFoundException, FileDeleteException {

        // Given
        rolSaga.setImageUrl("https://cloudinary.com/old-image.png");

        when(repository.findById(rolSaga.getId()))
                .thenReturn(Optional.of(rolSaga));
        when(appProperties.defaultImages()).thenReturn(defaultImages);
        when(defaultImages.rolSaga()).thenReturn("https://example.com/default-rol-saga.png");

        // When
        service.delete(rolSaga.getId());

        // Then
        verify(repository).findById(rolSaga.getId());
        verify(fileService).deleteFile(rolSaga.getImageUrl());
        verify(repository).delete(rolSaga);
    }

    @Test
    void should_throw_exception_when_deleting_non_existing_rol_saga() {

        // Given
        String id = "non-existent-id";

        when(repository.findById(id))
                .thenReturn(Optional.empty());

        // When & Then
        assertThrows(
                RolSagaNotFoundException.class,
                () -> service.delete(id));

        verify(repository).findById(id);

        verify(fileService, never()).deleteFile(anyString());
        verify(repository, never()).delete(any());
    }

    @Test
    void should_not_delete_rol_saga_when_image_deletion_fails()
            throws FileDeleteException {

        // Given
        rolSaga.setImageUrl("https://cloudinary.com/old-image.png");

        when(repository.findById(rolSaga.getId()))
                .thenReturn(Optional.of(rolSaga));

        when(appProperties.defaultImages()).thenReturn(defaultImages);
        when(defaultImages.rolSaga()).thenReturn("https://example.com/default-rol-saga.png");

        doThrow(new FileDeleteException("Error deleting image"))
                .when(fileService)
                .deleteFile(rolSaga.getImageUrl());

        // When & Then
        assertThrows(
                FileDeleteException.class,
                () -> service.delete(rolSaga.getId()));

        verify(repository).findById(rolSaga.getId());
        verify(fileService).deleteFile(rolSaga.getImageUrl());

        verify(repository, never()).delete(any());
    }

}
