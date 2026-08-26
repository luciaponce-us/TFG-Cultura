package com.tfg.cultura.api.suggestions.exception;

import org.slf4j.LoggerFactory;

import com.tfg.cultura.api.core.exception.NotFoundException;

public class SuggestionNotFoundException extends NotFoundException {
    public SuggestionNotFoundException(String suggestionId) {
        super("No se ha encontrado la sugerencia con id " + suggestionId,
                LoggerFactory.getLogger("suggestionsLogger"));
    }
}
