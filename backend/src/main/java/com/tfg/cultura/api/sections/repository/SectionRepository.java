package com.tfg.cultura.api.sections.repository;

import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.users.model.User;
import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SectionRepository extends MongoRepository<Section, String> {
	Optional<Section> findById(String id);
	Optional<Section> findByName(String name);
	List<Section> findAll();
	List<Section> findAllByNameContainingIgnoreCase(String name);
	Optional<Section> findByManagersContaining(User manager);
	Optional<Section> findByCollaboratorsContaining(User collaborator);
}
