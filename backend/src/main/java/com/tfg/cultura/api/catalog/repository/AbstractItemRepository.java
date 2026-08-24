package com.tfg.cultura.api.catalog.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.model.Item;

@NoRepositoryBean
public interface AbstractItemRepository<T extends Item> extends MongoRepository<T, String> {
    List<T> findByNameContainingIgnoreCase(String name);
    Iterable<T> findAllByCategoriesContaining(Category category);
}
