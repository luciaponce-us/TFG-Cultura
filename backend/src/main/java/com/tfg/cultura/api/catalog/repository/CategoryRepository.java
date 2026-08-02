package com.tfg.cultura.api.catalog.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tfg.cultura.api.catalog.model.Category;

public interface CategoryRepository extends MongoRepository<Category, String> {
    
}
