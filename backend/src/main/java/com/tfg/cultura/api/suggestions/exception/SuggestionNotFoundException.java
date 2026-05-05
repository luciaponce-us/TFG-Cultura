package com.tfg.cultura.api.suggestions.exception;

public class SuggestionNotFoundException extends RuntimeException {
    public SuggestionNotFoundException(String suggestionId) {
        super(String.format("No se ha encontrado la sugerencia con id %s", suggestionId));
    }
}
