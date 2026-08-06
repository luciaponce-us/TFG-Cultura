package com.tfg.cultura.api.catalog.controller;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

import com.tfg.cultura.api.catalog.exception.category.CategoryAlreadyExistsException;
import com.tfg.cultura.api.catalog.exception.category.CategoryNotFoundException;
import com.tfg.cultura.api.catalog.exception.CatalogExceptionHandler;
import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.service.CategoryDeletingService;
import com.tfg.cultura.api.catalog.service.CategoryService;
import com.tfg.cultura.api.utils.BaseControllerTest;

class CategoryControllerTest extends BaseControllerTest {

    @Mock
    private CategoryService categoryService;

    @Mock
    private CategoryDeletingService categoryDeletingService;

    private static final String BASE_URL = "/api/catalog/categories";
    private static final String CATEGORY_URL = BASE_URL + "/{id}";

    private Category category;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        CategoryController controller = new CategoryController(categoryService, categoryDeletingService);
        mockMvc = buildMockMvc(controller, CatalogExceptionHandler.class);
        initTestData();
    }

    private void initTestData() {
        category = CatalogFactory.validCategory();
    }

    // ====================== CREATE ======================

    @Test
    void should_create_category_successfully() throws Exception {
        when(categoryService.createCategory(anyString())).thenReturn(category);

        mockMvc.perform(post(BASE_URL)
                .param("name", category.getName())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value(category.getName()));

        verify(categoryService).createCategory(category.getName());
    }

    @Test
    void should_return_conflict_when_category_already_exists() throws Exception {
        when(categoryService.createCategory(anyString()))
                .thenThrow(new CategoryAlreadyExistsException(category.getName()));

        mockMvc.perform(post(BASE_URL)
                .param("name", category.getName())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.error").value("Category Already Exists"));

        verify(categoryService).createCategory(category.getName());
    }

    // ====================== GET ALL ======================

    @Test
    void should_get_all_categories() throws Exception {
        when(categoryService.findAllCategories()).thenReturn(List.of(category));

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].id").value(category.getId()))
                .andExpect(jsonPath("$[0].name").value(category.getName()));

        verify(categoryService).findAllCategories();
    }

    @Test
    void should_get_empty_category_list_when_no_categories_exist() throws Exception {
        when(categoryService.findAllCategories()).thenReturn(List.of());

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        verify(categoryService).findAllCategories();
    }

    // ====================== UPDATE ======================

    @Test
    void should_update_category_successfully() throws Exception {
        Category updatedCategory = Category.builder()
                .id(category.getId())
                .name("Updated Category")
                .color(category.getColor())
                .build();

        when(categoryService.updateCategory(anyString(), anyString())).thenReturn(updatedCategory);

        mockMvc.perform(put(CATEGORY_URL, category.getId())
                .param("name", updatedCategory.getName())
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(category.getId()))
                .andExpect(jsonPath("$.name").value(updatedCategory.getName()));

        verify(categoryService).updateCategory(category.getId(), updatedCategory.getName());
    }

    @Test
    void should_return_404_when_updating_missing_category() throws Exception {
        when(categoryService.updateCategory(anyString(), anyString()))
                .thenThrow(new CategoryNotFoundException("Categoría no encontrada con ID: 99"));

        mockMvc.perform(put(CATEGORY_URL, "99")
                .param("name", "Updated Category")
                .contentType(MediaType.APPLICATION_FORM_URLENCODED))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Category Not Found"));

        verify(categoryService).updateCategory("99", "Updated Category");
    }

    // ====================== DELETE ======================

    @Test
    void should_delete_category_successfully() throws Exception {
        mockMvc.perform(delete(CATEGORY_URL, category.getId()))
                .andExpect(status().isNoContent());

        verify(categoryDeletingService).deleteCategory(category.getId());
    }

    @Test
    void should_return_404_when_deleting_missing_category() throws Exception {
        doThrow(new CategoryNotFoundException("Categoría no encontrada con ID: 99"))
                .when(categoryDeletingService).deleteCategory(anyString());

        mockMvc.perform(delete(CATEGORY_URL, "99"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").value("Category Not Found"));

        verify(categoryDeletingService).deleteCategory("99");
    }
}
