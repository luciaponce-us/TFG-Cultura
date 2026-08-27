package com.tfg.cultura.api.suggestions.repository;

import com.tfg.cultura.api.suggestions.model.Suggestion;
import com.tfg.cultura.api.suggestions.model.enumerators.SuggestionType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SuggestionRepository extends MongoRepository<Suggestion, String>, SuggestionRespositoryCustom {
	void deleteByAuthorId(String authorId);

	Page<Suggestion> findAllWithFilters(SuggestionType type, String text, Boolean supportedByAdmins,
			Boolean mySuggestions, Pageable pageable);
}
