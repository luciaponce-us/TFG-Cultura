package com.tfg.cultura.api.categories.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tfg.cultura.api.categories.exception.CategoryAlreadyExistsException;
import com.tfg.cultura.api.categories.exception.CategoryNotFoundException;
import com.tfg.cultura.api.categories.factory.CategoryFactory;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.categories.model.dto.CategoryRequest;
import com.tfg.cultura.api.categories.service.CategoryDeletingService;
import com.tfg.cultura.api.categories.service.CategoryService;
import com.tfg.cultura.api.utils.BaseControllerTest;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

class CategoryControllerTest extends BaseControllerTest {

	@Mock
	private CategoryService categoryService;

	@Mock
	private CategoryDeletingService categoryDeletingService;

	private static final String BASE_URL = "/api/categories";
	private static final String CATEGORY_URL = BASE_URL + "/{id}";

	private Category category;
	private CategoryRequest categoryRequest;

	@BeforeEach
	void setup() {
		MockitoAnnotations.openMocks(this);
		CategoryController controller = new CategoryController(categoryService, categoryDeletingService);
		mockMvc = buildMockMvc(controller);
		initTestData();
	}

	private void initTestData() {
		category = CategoryFactory.validCategory();
		categoryRequest = CategoryFactory.validCategoryRequest();
	}

	// ====================== CREATE ======================

	@Test
	void should_create_category_successfully() throws Exception {
		when(categoryService.createCategory(any(CategoryRequest.class))).thenReturn(category);

		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(toJson(categoryRequest)))
				.andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(category.getId()))
				.andExpect(jsonPath("$.name").value(category.getName()))
				.andExpect(jsonPath("$.color").value(category.getColor()));

		verify(categoryService).createCategory(any(CategoryRequest.class));
	}

	@Test
	void should_return_conflict_when_category_already_exists() throws Exception {
		when(categoryService.createCategory(any(CategoryRequest.class)))
				.thenThrow(new CategoryAlreadyExistsException(category.getName()));

		mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(toJson(categoryRequest)))
				.andExpect(status().isConflict()).andExpect(jsonPath("$.message").exists());

		verify(categoryService).createCategory(any(CategoryRequest.class));
	}

	// ====================== GET ALL ======================

	@Test
	void should_get_all_categories() throws Exception {
		when(categoryService.findAllCategories()).thenReturn(List.of(category));

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(1))
				.andExpect(jsonPath("$[0].id").value(category.getId()))
				.andExpect(jsonPath("$[0].name").value(category.getName()));

		verify(categoryService).findAllCategories();
	}

	@Test
	void should_get_empty_category_list_when_no_categories_exist() throws Exception {
		when(categoryService.findAllCategories()).thenReturn(List.of());

		mockMvc.perform(get(BASE_URL)).andExpect(status().isOk()).andExpect(jsonPath("$.length()").value(0));

		verify(categoryService).findAllCategories();
	}

	// ====================== UPDATE ======================

	@Test
	void should_update_category_successfully() throws Exception {
		Category updatedCategory = Category.builder().id(category.getId()).name("Updated Category")
				.color(category.getColor()).build();

		when(categoryService.updateCategory(anyString(), any(CategoryRequest.class))).thenReturn(updatedCategory);

		CategoryRequest updateRequest = CategoryRequest.builder().name(updatedCategory.getName())
				.color(updatedCategory.getColor()).build();

		mockMvc.perform(put(CATEGORY_URL, category.getId()).contentType(MediaType.APPLICATION_JSON)
				.content(toJson(updateRequest))).andExpect(status().isOk())
				.andExpect(jsonPath("$.id").value(category.getId()))
				.andExpect(jsonPath("$.name").value(updatedCategory.getName()))
				.andExpect(jsonPath("$.color").value(updatedCategory.getColor()));

		verify(categoryService).updateCategory(eq(category.getId()), any(CategoryRequest.class));
	}

	@Test
	void should_return_404_when_updating_missing_category() throws Exception {
		when(categoryService.updateCategory(anyString(), any(CategoryRequest.class)))
				.thenThrow(new CategoryNotFoundException("Categoría no encontrada con ID: 99"));

		CategoryRequest updateRequest = CategoryRequest.builder().name("Updated Category").color("#000000").build();

		mockMvc.perform(put(CATEGORY_URL, "99").contentType(MediaType.APPLICATION_JSON).content(toJson(updateRequest)))
				.andExpect(status().isNotFound()).andExpect(jsonPath("$.message").exists());

		verify(categoryService).updateCategory(eq("99"), any(CategoryRequest.class));
	}

	// ====================== DELETE ======================

	@Test
	void should_delete_category_successfully() throws Exception {
		mockMvc.perform(delete(CATEGORY_URL, category.getId())).andExpect(status().isNoContent());

		verify(categoryDeletingService).deleteCategory(category.getId());
	}

	@Test
	void should_return_404_when_deleting_missing_category() throws Exception {
		doThrow(new CategoryNotFoundException("Categoría no encontrada con ID: 99")).when(categoryDeletingService)
				.deleteCategory(anyString());

		mockMvc.perform(delete(CATEGORY_URL, "99")).andExpect(status().isNotFound())
				.andExpect(jsonPath("$.message").exists());

		verify(categoryDeletingService).deleteCategory("99");
	}
}
