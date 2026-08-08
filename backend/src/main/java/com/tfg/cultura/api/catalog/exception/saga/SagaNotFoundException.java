package com.tfg.cultura.api.catalog.exception.saga;

public class SagaNotFoundException extends RuntimeException {
    public SagaNotFoundException(String message) {
        super(message);
    }
    
}
