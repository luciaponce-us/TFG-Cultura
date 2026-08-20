package com.tfg.cultura.api.catalog.exception.rolsaga;

public class RolSagaAlreadyExistsException extends RuntimeException {
    public RolSagaAlreadyExistsException(String name) {
        super("Ya existe una saga de rol con el nombre: " + name);
    }
    
}
