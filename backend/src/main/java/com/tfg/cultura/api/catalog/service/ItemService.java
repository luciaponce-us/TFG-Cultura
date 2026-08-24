package com.tfg.cultura.api.catalog.service;

import java.util.List;
import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.model.Item;
import com.tfg.cultura.api.catalog.repository.ItemRepository;
import com.tfg.cultura.api.sections.exception.SectionNotFoundException;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.service.SectionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final SectionService sectionService;
    private final CategoryService categoryService;

    public List<String> getItemTypes() {
        return List.of(
            "Libro",
            "Juego de mesa",
            "Película",
            "Serie",
            "Videojuego",
            "Juego de rol");
    }

    public Page<Item> getAllItems(Pageable pageable, String nameContains, Set<String> categoryIds, String sectionId) throws SectionNotFoundException {
        
        Section section = sectionId == null ? null : sectionService.findSectionById(sectionId);
        Set<Category> categories = categoryIds == null ? Set.of() : categoryService.findCategoriesByIds(categoryIds);
        String nameFilter = nameContains == null ? "*" : nameContains;

        return itemRepository.findAllByNameContainingIgnoreCaseAndCategoriesContainingAndSection(
            nameFilter,
            categories,
            section,
            pageable);
    }

}
