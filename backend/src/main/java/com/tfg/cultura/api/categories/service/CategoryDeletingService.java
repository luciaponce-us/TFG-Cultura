package com.tfg.cultura.api.categories.service;

import com.tfg.cultura.api.catalog.service.BookService;
import com.tfg.cultura.api.categories.exception.CategoryNotFoundException;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.categories.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
