package com.tfg.cultura.api.suggestions.exception;

import com.tfg.cultura.api.core.exception.ApiException;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class SelfSupportSuggestionException extends ApiException {
	public SelfSupportSuggestionException() {
		super("No puedes apoyar tu propia sugerencia", LoggerFactory.getLogger("suggestionsLogger"),
				HttpStatus.BAD_REQUEST);
	}

}
