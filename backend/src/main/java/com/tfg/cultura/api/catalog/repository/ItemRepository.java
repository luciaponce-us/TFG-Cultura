package com.tfg.cultura.api.catalog.repository;

import com.tfg.cultura.api.catalog.model.Item;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.sections.model.Section;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ItemRepository extends MongoRepository<Item, String> {
	Page<Item> findAllByNameContainingIgnoreCaseAndCategoriesContainingAndSection(String name, Set<Category> categories,
			Section section, Pageable pageable);
}
