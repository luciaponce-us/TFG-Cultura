package com.tfg.cultura.api.categories.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.cultura.api.categories.exception.CategoryAlreadyExistsException;
import com.tfg.cultura.api.categories.exception.CategoryNotFoundException;
import com.tfg.cultura.api.categories.factory.CategoryFactory;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.categories.repository.CategoryRepository;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepository categoryRepository;

    @InjectMocks
    private CategoryService service;

    private Category category;
    private Category anotherCategory;

    @BeforeEach
    void setUp() {
        category = CategoryFactory.validCategory();
        anotherCategory = CategoryFactory.anotherValidCategory();
    }

    // CREATE

    @Test
    void should_create_category() {

        when(categoryRepository.existsByName(category.getName()))
                .thenReturn(false);

        when(categoryRepository.save(any(Category.class)))
                .thenReturn(category);

        Category result = service.createCategory(category.getName());

        assertEquals(category, result);

        verify(categoryRepository).save(any(Category.class));
        verify(categoryRepository).existsByName(category.getName());
    }

    @Test
    void should_throw_when_category_already_exists() {

        when(categoryRepository.existsByName(category.getName()))
                .thenReturn(true);

        assertThrows(
                CategoryAlreadyExistsException.class,
                () -> service.createCategory(category.getName()));

        verify(categoryRepository).existsByName(category.getName());
        verify(categoryRepository, never()).save(any());
    }

    // READ

    @Test
    void should_return_category_when_exists() {
        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));

        Category result = service.findCategoryById(category.getId());

        assertEquals(category, result);

        verify(categoryRepository).findById(category.getId());
    }

    @Test
    void should_throw_when_category_not_found() {

        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.empty());

        CategoryNotFoundException exception = assertThrows(
                CategoryNotFoundException.class,
                () -> service.findCategoryById(category.getId()));

        assertEquals(
                "Categoría no encontrada con ID: " + category.getId(),
                exception.getMessage());

        verify(categoryRepository).findById(category.getId());
    }

    @Test
    void should_return_categories_when_all_exist() {

        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));

        when(categoryRepository.findById(anotherCategory.getId()))
                .thenReturn(Optional.of(anotherCategory));

        Set<Category> result = service.findCategoriesByIds(Set.of(category.getId(), anotherCategory.getId()));

        assertEquals(2, result.size());
        assertTrue(result.contains(category));
        assertTrue(result.contains(anotherCategory));

        verify(categoryRepository).findById(category.getId());
        verify(categoryRepository).findById(anotherCategory.getId());
    }

    @Test
    void should_throw_when_any_category_does_not_exist() {
        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> service.findCategoriesByIds(Set.of(category.getId())));

        verify(categoryRepository).findById(category.getId());
    }

    @Test
    void should_return_all_categories_sorted_by_name() {

        List<Category> categories = List.of(
                category,
                anotherCategory);

        when(categoryRepository.findAllByOrderByNameAsc())
                .thenReturn(categories);

        List<Category> result = service.findAllCategories();

        assertEquals(categories, result);

        verify(categoryRepository).findAllByOrderByNameAsc();
    }

    // UPDATE

    @Test
    void should_update_category() {

        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.of(category));

        when(categoryRepository.save(any(Category.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        Category result = service.updateCategory(category.getId(), "Science Fiction");

        assertEquals("Science Fiction", result.getName());
        assertEquals(category.getId(), result.getId());
        assertEquals("Science Fiction", result.getName());

        verify(categoryRepository).save(result);
    }

    @Test
    void should_throw_when_updating_non_existing_category() {

        when(categoryRepository.findById(category.getId()))
                .thenReturn(Optional.empty());

        assertThrows(
                CategoryNotFoundException.class,
                () -> service.updateCategory(category.getId(), "Science Fiction"));

        verify(categoryRepository).findById(category.getId());
        verify(categoryRepository, never()).save(any());
    }

}
