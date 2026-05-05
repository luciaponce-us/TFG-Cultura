package com.tfg.cultura.api.suggestions.exception;

public class SuggestionNotSupportedException extends RuntimeException {
    public SuggestionNotSupportedException() {
        super("No estás apoyando esta sugerencia");
    }
    
}
