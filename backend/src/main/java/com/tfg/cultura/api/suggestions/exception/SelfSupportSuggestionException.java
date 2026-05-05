package com.tfg.cultura.api.suggestions.exception;

public class SelfSupportSuggestionException extends RuntimeException {
    public SelfSupportSuggestionException() {
        super("No puedes apoyar tu propia sugerencia");
    }
    
}
