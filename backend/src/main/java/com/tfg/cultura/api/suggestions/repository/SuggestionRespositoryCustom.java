package com.tfg.cultura.api.suggestions.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tfg.cultura.api.suggestions.model.Suggestion;
import com.tfg.cultura.api.suggestions.model.enumerators.SuggestionType;

public interface SuggestionRespositoryCustom {
    Page<Suggestion> findAllWithFilters(SuggestionType type, String text, Boolean supportedByAdmins, Pageable pageable);
}
