package com.tfg.cultura.api.catalog.service;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.tfg.cultura.api.catalog.exception.CategoryNotFoundException;
import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryService {
    private final CategoryRepository categoryRepository;

    private static final Logger logger = LoggerFactory.getLogger("catalogLogger");

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
}
