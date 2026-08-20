package com.tfg.cultura.api.catalog.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.tfg.cultura.api.catalog.model.RolSaga;

public interface RolSagaRepository extends MongoRepository<RolSaga, String> {
    boolean existsByName(String name);
}
