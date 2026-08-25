package com.tfg.cultura.api.categories.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tfg.cultura.api.categories.exception.CategoryAlreadyExistsException;
import com.tfg.cultura.api.categories.exception.CategoryNotFoundException;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.categories.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    
    private final CategoryRepository categoryRepository;

    private static final Logger logger = LoggerFactory.getLogger("catalogLogger");

    // CREATE

    public Category createCategory(String name) throws CategoryAlreadyExistsException {
        boolean exists = categoryRepository.existsByName(name);
        if (exists) {
            logger.error("Ya existe una categoría con el nombre: {}", name);
            throw new CategoryAlreadyExistsException(name);
        }

        Category category = Category.builder()
                .name(name)
                .build();
        return categoryRepository.save(category);
    }

    // READ

    public Category findCategoryById(String id) throws CategoryNotFoundException {
        Optional<Category> category = categoryRepository.findById(id);
        if (category.isEmpty()) {
            logger.error("Categoría no encontrada con ID: {}", id);
            throw new CategoryNotFoundException("Categoría no encontrada con ID: " + id);
        }
        return category.get();
    }

    public Set<Category> findCategoriesByIds(Set<String> ids) throws CategoryNotFoundException {
        Set<Category> categories = new HashSet<>();
        for (String id : ids) {
            categories.add(findCategoryById(id));
        }
        return categories;
    }

    public List<Category> findAllCategories() {
        return categoryRepository.findAllByOrderByNameAsc();
    }

    // UPDATE

    public Category updateCategory(String id, String name) throws CategoryNotFoundException {
        Category category = findCategoryById(id);
        category.setName(name);
        return categoryRepository.save(category);
    }

    // DELETE: In CategoryDeletingService to avoid circular dependency with item services

}
