package com.tfg.cultura.api.catalog.exception.rolsaga;

import com.tfg.cultura.api.core.utils.LoggerSanitizer;

public class RolSagaNotFoundException extends RuntimeException {

    public RolSagaNotFoundException(String id) {
        super("Saga de rol con id " + LoggerSanitizer.sanitize(id) + " no encontrada");
    }
    
}
