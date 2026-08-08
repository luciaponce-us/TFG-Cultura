package com.tfg.cultura.api.catalog.service;

import java.util.Set;
import java.util.function.Function;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.catalog.exception.item.ItemNotFoundException;
import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.model.Item;
import com.tfg.cultura.api.catalog.model.dto.ItemCreateRequest;
import com.tfg.cultura.api.catalog.repository.ItemRepository;
import com.tfg.cultura.api.core.exception.FileUploadException;
import com.tfg.cultura.api.core.model.dto.FileUploadRequest;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.core.utils.LoggerSanitizer;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.service.SectionService;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public abstract class AbstractItemService<T extends Item, R extends ItemRepository<T>, C extends ItemCreateRequest, RES>
        implements ItemServiceInterface<T, C, RES> {

    private static final Integer MAX_IMAGE_SIZE_MB = 2;

    private static final Logger logger = LoggerFactory.getLogger("catalogLogger");

    protected final R repository;
    private final SectionService sectionService;
    private final CategoryService categoryService;
    private final FileService fileService;
    private final Function<T, RES> mapper;

    @Override
    public T findById(String id) throws ItemNotFoundException {
        return repository.findById(id)
                .orElseThrow(() -> {
                    logger.error("Item no encontrado con ID: {}", sanitize(id));
                    return new ItemNotFoundException("Item no encontrado con ID: " + sanitize(id));
                });

    }

    @Override
    public RES getById(String id) throws ItemNotFoundException {
        T item = findById(id);
        return mapper.apply(item);
    }

    @Override
    public Page<RES> getAll(Pageable pageable) {
        Page<T> items = repository.findAll(pageable);
        return items.map(mapper);
    }

    @Override
    @Transactional
    public RES create(C request, MultipartFile image) throws FileUploadException, IllegalArgumentException {

        T item = createEntity();

        fillItemFields(item, request, getLoanDays(request));

        validateItem(item);

        fillSpecificFields(item, request);

        validate(item);

        String imageUrl = uploadImage(item.getId(), image, getImageFolder(), getDefaultImageUrl());
        item.setImageUrl(imageUrl);

        T savedItem = repository.save(item);

        postCreationActions(savedItem);

        return mapper.apply(savedItem);
    }

    protected abstract T createEntity();

    protected abstract void fillSpecificFields(T item, C request);

    protected abstract Integer getLoanDays(C request);

    protected abstract String getImageFolder();

    protected abstract String getDefaultImageUrl();

    private void validateItem(T item) {
        checkAvailableCopies(item.getAvailableCopies(), item.getCopies());
    };

    protected void validate(T item) {
        // Default validation logic can be implemented here if needed
    }

    protected void postCreationActions(T item) {
        // Default implementation - can be overridden by subclasses
    }

    @Override
    @Transactional
    public void delete(String id) {
        T item = findById(id);
        preDeletionActions(item);
        repository.delete(item);
    }

    protected void preDeletionActions(T item) {
        // Default implementation - can be overridden by subclasses
    }

    @Override
    public RES update(String id, C request, MultipartFile image)
            throws ItemNotFoundException, FileUploadException, IllegalArgumentException {
        T existingItem = findById(id);

        fillItemFields(existingItem, request, getLoanDays(request));

        validateItem(existingItem);

        fillSpecificFields(existingItem, request);

        validate(existingItem);

        T updatedItem = repository.save(existingItem);

        updateImage(existingItem, image, getImageFolder(), getDefaultImageUrl());

        postUpdateActions(existingItem, updatedItem);

        return mapper.apply(updatedItem);
    }

    protected void postUpdateActions(T oldItem, T updatedItem)
            throws ItemNotFoundException, IllegalArgumentException {
        // Default implementation - can be overridden by subclasses
    };

    private void checkAvailableCopies(Integer availableCopies, Integer copies) throws IllegalArgumentException {
        if (availableCopies < 0) {
            logger.error("El número de copias disponibles no puede ser menor que 0");
            throw new IllegalArgumentException("El número de copias disponibles no puede ser menor que 0");
        }
        if (availableCopies > copies) {
            logger.error("El número de copias disponibles no puede ser mayor que el número total de copias");
            throw new IllegalArgumentException(
                    "El número de copias disponibles no puede ser mayor que el número total de copias");
        }
    }

    private void fillItemFields(T item, C request, Integer loanDays) {
        checkAvailableCopies(request.getAvailableCopies(), request.getCopies());

        Section section = sectionService.findSectionById(request.getSectionId());
        Set<Category> categories = categoryService.findCategoriesByIds(request.getCategoriesIds());

        item.setName(sanitize(request.getName()));
        item.setDescription(sanitize(request.getDescription()));
        item.setCondition(request.getCondition());
        item.setComments(sanitize(request.getComments()));
        item.setLoanAvailable(request.getLoanAvailable());
        item.setPublicated(request.getPublicated());
        item.setPurchasedAt(request.getPurchasedAt());
        item.setPrice(request.getPrice());
        item.setCopies(request.getCopies());
        item.setAvailableCopies(request.getAvailableCopies());
        item.setLoanDays(loanDays);
        item.setSection(section);
        item.setCategories(categories);
    }

    private String uploadImage(String itemId, MultipartFile image, String folder, String defaultImageUrl)
            throws FileUploadException {
        if (image != null && !image.isEmpty()) {
            String id = LoggerSanitizer.sanitize(itemId);

            fileService.validateImageSize(image, MAX_IMAGE_SIZE_MB);
            MultipartFile resizedImage = fileService.resizeImage(image, 400, 600);

            FileUploadRequest request = FileUploadRequest.builder()
                    .file(resizedImage)
                    .folder(folder)
                    .publicId("item_" + id)
                    .resourceType("image")
                    .build();
            String imageUrl = fileService.uploadFile(request);
            logger.info("Se ha subido la imagen {} para el item con id {}", imageUrl, id);

            return imageUrl;
        }
        return defaultImageUrl;
    }

    private void updateImage(T item, MultipartFile image, String folder, String defaultImageUrl)
            throws FileUploadException {
        String oldImageUrl = item.getImageUrl();
        if (oldImageUrl != null && !oldImageUrl.equals(defaultImageUrl)) {
            fileService.deleteFile(oldImageUrl);
        }
        String newImageUrl = uploadImage(item.getId(), image, folder, defaultImageUrl);
        item.setImageUrl(newImageUrl);
    }

    public void removeCategory(Category category) {
        repository.findAllByCategoriesContaining(category)
                .forEach(item -> {
                    item.getCategories().remove(category);
                    repository.save(item);
                });
    }
}
