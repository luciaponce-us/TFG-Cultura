package com.tfg.cultura.api.suggestions.exception;

import com.tfg.cultura.api.core.exception.NotFoundException;
import org.slf4j.LoggerFactory;

public class SuggestionNotFoundException extends NotFoundException {
	public SuggestionNotFoundException(String suggestionId) {
		super("No se ha encontrado la sugerencia con id " + suggestionId, LoggerFactory.getLogger("suggestionsLogger"));
	}
}
