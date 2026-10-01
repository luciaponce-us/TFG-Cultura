package com.tfg.cultura.api.catalog.repository;

import com.tfg.cultura.api.catalog.model.Saga;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SagaRepository extends MongoRepository<Saga, String> {
	boolean existsByName(String name);
	Saga findByName(String name);
	List<Saga> findAllByOrderByNameAsc();
}
