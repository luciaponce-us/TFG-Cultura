package com.tfg.cultura.api.catalog.repository;

import java.util.List;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tfg.cultura.api.catalog.model.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {

    boolean existsByName(String name);

    List<Category> findAllByOrderByNameAsc();
    
}
