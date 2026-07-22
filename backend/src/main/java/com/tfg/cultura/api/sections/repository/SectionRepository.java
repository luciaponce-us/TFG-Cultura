package com.tfg.cultura.api.sections.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import com.tfg.cultura.api.sections.model.Section;

public interface SectionRepository extends MongoRepository<Section, String> {
    Optional<Section> findByName(String name);
    Page<Section> findByNameContainingIgnoreCase(String name, Pageable pageable);
}
