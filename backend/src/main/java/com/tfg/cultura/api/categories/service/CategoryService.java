package com.tfg.cultura.api.categories.service;

import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.categories.model.dto.CategoryRequest;
import com.tfg.cultura.api.categories.repository.CategoryRepository;
import com.tfg.cultura.api.core.exception.DuplicationException;
import com.tfg.cultura.api.core.exception.NotFoundException;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CategoryService {

	private final CategoryRepository categoryRepository;

	private static final Logger logger = LoggerFactory.getLogger("categoriesLogger");

	// CREATE

	public Category createCategory(CategoryRequest request) throws DuplicationException {
		String name = request.getName();
		String color = request.getColor();

		boolean exists = categoryRepository.existsByName(name);
		if (exists) {
			throw new DuplicationException(logger, Map.of("name","Ya existe una categoría con el nombre: " + name));
		}

		Category category = Category.builder().name(name).color(color).build();

		return categoryRepository.save(category);
	}

	// READ

	public Category findCategoryById(String id) throws NotFoundException {
		Optional<Category> category = categoryRepository.findById(id);
		if (category.isEmpty()) {
			throw new NotFoundException("Categoría no encontrada con ID: " + id, logger);
		}
		return category.get();
	}

	public Set<Category> findCategoriesByIds(Set<String> ids) throws NotFoundException {
		Set<Category> categories = new HashSet<>();
		if (ids == null) {
			return categories;
		}

		for (String id : ids) {
			categories.add(findCategoryById(id));
		}
		return categories;
	}

	public List<Category> findAllCategories() {
		return categoryRepository.findAllByOrderByNameAsc();
	}

	// UPDATE

	public Category updateCategory(String id, CategoryRequest request) throws NotFoundException {
		Category category = findCategoryById(id);
		category.setName(request.getName());
		category.setColor(request.getColor());
		return categoryRepository.save(category);
	}

	// DELETE: In CategoryDeletingService to avoid circular dependency with item
	// services

}
