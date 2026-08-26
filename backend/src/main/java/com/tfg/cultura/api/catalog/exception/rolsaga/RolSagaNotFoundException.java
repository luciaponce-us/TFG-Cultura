package com.tfg.cultura.api.catalog.exception.rolsaga;

import com.tfg.cultura.api.core.utils.LoggerSanitizer;
import org.slf4j.LoggerFactory;
import com.tfg.cultura.api.core.exception.NotFoundException;

public class RolSagaNotFoundException extends NotFoundException {

    public RolSagaNotFoundException(String id) {
        super("Saga de rol con id " + LoggerSanitizer.sanitize(id) + " no encontrada",
            LoggerFactory.getLogger("catalogLogger"));
    }
    
}
