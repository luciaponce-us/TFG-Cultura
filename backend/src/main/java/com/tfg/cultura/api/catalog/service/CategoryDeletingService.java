package com.tfg.cultura.api.catalog.service;

import org.springframework.stereotype.Service;

import com.tfg.cultura.api.catalog.exception.category.CategoryNotFoundException;
import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CategoryDeletingService {
    private final CategoryRepository categoryRepository;
    private final CategoryService categoryService;

    private final BookService bookService;

    public void deleteCategory(String id) throws CategoryNotFoundException {
        Category category = categoryService.findCategoryById(id);
        bookService.removeCategory(category);
        categoryRepository.delete(category);
    }
    
}
