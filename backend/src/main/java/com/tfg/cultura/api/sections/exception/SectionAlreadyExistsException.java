package com.tfg.cultura.api.sections.exception;

import java.util.Map;

import org.slf4j.LoggerFactory;

import com.tfg.cultura.api.core.exception.DuplicationException;

public class SectionAlreadyExistsException extends DuplicationException {
    public SectionAlreadyExistsException(String name) {
        super(
            LoggerFactory.getLogger("sectionsLogger"),
            Map.of("name", "La sección con nombre " + name + " ya existe")
        );
    }
    
}
