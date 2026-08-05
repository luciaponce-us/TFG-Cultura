package com.tfg.cultura.api.catalog.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.cultura.api.catalog.exception.category.CategoryNotFoundException;
import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryDeletingServiceTest {

        @Mock
    private CategoryRepository categoryRepository;

    @Mock
    private CategoryService categoryService;

    @Mock
    private BookService bookService;

    @InjectMocks
    private CategoryDeletingService service;

    private Category category;

    @BeforeEach
    void setUp() {
        category = Category.builder()
                .id("category-id")
                .name("Fantasy")
                .build();
    }

    @Test
    void should_delete_category() {
        when(categoryService.findCategoryById("category-id"))
                .thenReturn(category);

        service.deleteCategory("category-id");

        verify(categoryService).findCategoryById("category-id");
        verify(bookService).removeCategory(category);
        verify(categoryRepository).delete(category);

        verifyNoMoreInteractions(categoryRepository, bookService, categoryService);
    }

    @Test
    void should_throw_when_category_does_not_exist() {
        when(categoryService.findCategoryById("category-id"))
                .thenThrow(new CategoryNotFoundException("Categoría no encontrada"));

        assertThrows(
                CategoryNotFoundException.class,
                () -> service.deleteCategory("category-id"));

        verify(categoryService).findCategoryById("category-id");
        verify(bookService, never()).removeCategory(any());
        verify(categoryRepository, never()).delete(any());
    }
    
}
