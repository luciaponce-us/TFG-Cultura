package com.tfg.cultura.api.catalog.repository;

import com.tfg.cultura.api.catalog.model.RolSaga;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface RolSagaRepository extends MongoRepository<RolSaga, String> {
	boolean existsByNameAndIdNot(String name, String id);
}
