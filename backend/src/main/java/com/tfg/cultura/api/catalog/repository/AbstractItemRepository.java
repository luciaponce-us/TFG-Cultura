package com.tfg.cultura.api.catalog.repository;

import com.tfg.cultura.api.catalog.model.Item;
import com.tfg.cultura.api.categories.model.Category;
import java.util.List;
import java.util.Set;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractItemRepository<T extends Item> extends MongoRepository<T, String> {
	List<T> findByNameContainingIgnoreCase(String name);
	Iterable<T> findAllByCategoriesContaining(Category category);
	Page<T> findAllByNameContainingIgnoreCase(String name, Pageable pageable);
	Page<T> findAllByCategoriesContaining(Set<Category> categories, Pageable pageable);
	Page<T> findAllByNameContainingIgnoreCaseAndCategoriesContaining(String name, Set<Category> categories,
			Pageable pageable);
}
