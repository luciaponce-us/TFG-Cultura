package com.tfg.cultura.api.sections.exception;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

import org.slf4j.LoggerFactory;

import com.tfg.cultura.api.core.exception.NotFoundException;

public class SectionNotFoundException extends NotFoundException {
    public SectionNotFoundException(String id) {
        super("Sección no encontrada con id: " + sanitize(id), LoggerFactory.getLogger("sectionsLogger"));
    }
    
}
