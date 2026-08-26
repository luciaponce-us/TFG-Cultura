package com.tfg.cultura.api.catalog.exception.saga;

import org.slf4j.LoggerFactory;

import com.tfg.cultura.api.core.exception.NotFoundException;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

public class SagaNotFoundException extends NotFoundException {
    public SagaNotFoundException(String id) {
        super("No se ha encontrado la saga con id: " + sanitize(id), LoggerFactory.getLogger("catalogLogger"));
    }

}
