package com.tfg.cultura.api.catalog.service;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

import com.tfg.cultura.api.catalog.model.Item;
import com.tfg.cultura.api.catalog.model.dto.ItemRequest;
import com.tfg.cultura.api.catalog.repository.AbstractItemRepository;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.categories.service.CategoryService;
import com.tfg.cultura.api.core.exception.NotFoundException;
import com.tfg.cultura.api.core.exception.file.FileUploadException;
import com.tfg.cultura.api.core.model.dto.FileUploadRequest;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.service.SectionService;
import java.util.Set;
import java.util.function.Function;
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
public abstract class AbstractItemService<T extends Item, R extends AbstractItemRepository<T>, C extends ItemRequest, S>
		implements
			ItemServiceInterface<T, C, S> {

	protected static final Logger logger = LoggerFactory.getLogger("catalogLogger");

	protected final R repository;
	private final SectionService sectionService;
	protected final CategoryService categoryService;
	private final FileService fileService;
	private final Function<T, S> mapper;

	@Override
	public T findById(String id) throws NotFoundException {
		return repository.findById(id).orElseThrow(() -> {
			logger.error("Item no encontrado con ID: {}", sanitize(id));
			return new NotFoundException("Item no encontrado con ID: " + sanitize(id), logger);
		});

	}

	@Override
	public S getById(String id) throws NotFoundException {
		T item = findById(id);
		return mapper.apply(item);
	}

	@Override
	public Page<S> getAll(Pageable pageable, String nameContains, Set<String> categoryIds) {
		Set<Category> categories = categoryIds == null || categoryIds.isEmpty()
				? null
				: categoryService.findCategoriesByIds(categoryIds);

		String nameFilter = nameContains == null ? "*" : nameContains;

		Page<T> items;
		if (nameContains == null && categories == null) {
			items = repository.findAll(pageable);
		} else if (nameContains == null) {
			items = repository.findAllByCategoriesContaining(categories, pageable);
		} else if (categories == null) {
			items = repository.findAllByNameContainingIgnoreCase(nameFilter, pageable);
		} else {
			items = repository.findAllByNameContainingIgnoreCaseAndCategoriesContaining(nameFilter, categories,
					pageable);
		}

		return items.map(mapper);
	}

	@Override
	@Transactional
	public S create(C request, MultipartFile image) throws FileUploadException, IllegalArgumentException {

		T item = createEntity();

		fillItemFields(item, request, getLoanDays(request));

		validateItem(item);

		fillSpecificFields(item, request);

		validate(item);

		T savedItem = repository.save(item);

		if (image == null || image.isEmpty()) {
			postCreationActions(savedItem);
			return mapper.apply(savedItem);
		} else {
			FileUploadRequest imageRequest = FileUploadRequest.builder().file(image).folder(getImageFolder())
					.className("item").id(savedItem.getId()).width(400).height(600).defaultFileUrl(getDefaultImageUrl())
					.field("imageUrl").build();

			String imageUrl = fileService.uploadImage(imageRequest, logger);

			savedItem.setImageUrl(imageUrl);
			T savedItemWithImage = repository.save(savedItem);

			postCreationActions(savedItemWithImage);

			return mapper.apply(savedItemWithImage);
		}
	}

	protected abstract T createEntity();

	protected abstract void fillSpecificFields(T item, C request);

	protected abstract Integer getLoanDays(C request);

	protected abstract String getImageFolder();

	protected abstract String getDefaultImageUrl();

	private void validateItem(T item) {
		checkAvailableCopies(item.getAvailableCopies(), item.getCopies());
	}

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
		if (item.getImageUrl() != null && !item.getImageUrl().equals(getDefaultImageUrl())) {
			fileService.deleteFile(item.getImageUrl());
		}
		repository.delete(item);
	}

	protected void preDeletionActions(T item) {
		// Default implementation - can be overridden by subclasses
	}

	@Override
	@Transactional
	public S update(String id, C request, MultipartFile image)
			throws NotFoundException, FileUploadException, IllegalArgumentException {
		T existingItem = findById(id);

		fillItemFields(existingItem, request, getLoanDays(request));

		validateItem(existingItem);

		fillSpecificFields(existingItem, request);

		validate(existingItem);

		T updatedItem = repository.save(existingItem);

		if (image == null || image.isEmpty()) {
			postUpdateActions(existingItem, updatedItem);
			return mapper.apply(updatedItem);
		} else {
			FileUploadRequest imageRequest = FileUploadRequest.builder().file(image).folder(getImageFolder())
					.className("item").id(id).width(400).height(600).defaultFileUrl(getDefaultImageUrl())
					.field("imageUrl").resourceType("image").build();

			String newImageUrl = fileService.updateImage(existingItem.getImageUrl(), imageRequest, logger);
			updatedItem.setImageUrl(newImageUrl);
			T updatedItemWithImage = repository.save(updatedItem);

			postUpdateActions(existingItem, updatedItemWithImage);

			return mapper.apply(updatedItemWithImage);
		}
	}

	protected void postUpdateActions(T oldItem, T updatedItem) throws NotFoundException, IllegalArgumentException {
		// Default implementation - can be overridden by subclasses
	}

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

	public void removeCategory(Category category) {
		repository.findAllByCategoriesContaining(category).forEach(item -> {
			item.getCategories().remove(category);
			repository.save(item);
		});
	}

	public void setLoanUnavailable(T item) {
		item.setLoanAvailable(false);
		repository.save(item);
	}
}
