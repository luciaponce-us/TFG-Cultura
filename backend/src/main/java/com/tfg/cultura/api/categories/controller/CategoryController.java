package com.tfg.cultura.api.categories.controller;

import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.categories.service.CategoryDeletingService;
import com.tfg.cultura.api.categories.service.CategoryService;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog/categories")
@RequiredArgsConstructor
@Tag(name = "Catalog - Categories", description = "Gestión de categorías")
public class CategoryController {

	private final CategoryService categoryService;
	private final CategoryDeletingService categoryDeletingService;

	@PostMapping
	public ResponseEntity<Category> createCategory(@RequestParam String name) {
		Category category = categoryService.createCategory(name);
		return ResponseEntity.status(HttpStatus.CREATED).body(category);
	}

	@GetMapping
	public ResponseEntity<List<Category>> getAllCategories() {
		List<Category> categories = categoryService.findAllCategories();
		return ResponseEntity.status(HttpStatus.OK).body(categories);
	}

	@PutMapping("/{id}")
	public ResponseEntity<Category> updateCategory(@PathVariable String id, @RequestParam String name) {
		Category updatedCategory = categoryService.updateCategory(id, name);
		return ResponseEntity.status(HttpStatus.OK).body(updatedCategory);
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<Void> deleteCategory(@PathVariable String id) {
		categoryDeletingService.deleteCategory(id);
		return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
	}

}
