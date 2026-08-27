package com.tfg.cultura.api.categories.repository;

import com.tfg.cultura.api.categories.model.Category;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CategoryRepository extends MongoRepository<Category, String> {

	boolean existsByName(String name);

	List<Category> findAllByOrderByNameAsc();

}
