package com.tfg.cultura.api.suggestions.exception;

public class SuggestionAlreadySupportedException extends RuntimeException {
    public SuggestionAlreadySupportedException(){
        super("Ya apoyas esta sugerencia");
    }
    
}
