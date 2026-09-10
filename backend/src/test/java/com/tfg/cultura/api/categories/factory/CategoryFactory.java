package com.tfg.cultura.api.categories.factory;

import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.categories.model.dto.CategoryRequest;

public class CategoryFactory {
	public static Category validCategory() {
		return Category.builder().id("1").name("Test Category").color("#000000").build();
	}

	public static Category anotherValidCategory() {
		return Category.builder().id("2").name("Another Test Category").color("#000000").build();
	}

	public static CategoryRequest validCategoryRequest() {
		return CategoryRequest.builder().name("Test Category").color("#000000").build();
	}
}
