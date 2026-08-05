package com.tfg.cultura.api.catalog.exception.saga;

public class SagaAlreadyExistsException extends RuntimeException {
    public SagaAlreadyExistsException(String name) {
        super("Ya existe una saga con el nombre: " + name);
    }
    
}
