package com.tfg.cultura.api.suggestions.exception;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.tfg.cultura.api.core.exception.ApiException;

public class SelfSupportSuggestionException extends ApiException {
    public SelfSupportSuggestionException() {
        super(
                "No puedes apoyar tu propia sugerencia",
                LoggerFactory.getLogger("suggestionsLogger"),
                HttpStatus.BAD_REQUEST);
    }

}
