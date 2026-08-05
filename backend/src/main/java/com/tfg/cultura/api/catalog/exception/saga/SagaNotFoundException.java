package com.tfg.cultura.api.catalog.exception.saga;

public class SagaNotFoundException extends RuntimeException {
    public SagaNotFoundException(String id) {
        super("Saga no encontrada con ID: " + id);
    }
    
}
