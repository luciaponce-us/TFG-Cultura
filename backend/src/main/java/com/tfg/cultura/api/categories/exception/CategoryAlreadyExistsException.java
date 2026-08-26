package com.tfg.cultura.api.categories.exception;

import java.util.Map;

import org.slf4j.LoggerFactory;

import com.tfg.cultura.api.core.exception.DuplicationException;

public class CategoryAlreadyExistsException extends DuplicationException {
    public CategoryAlreadyExistsException(String name) {
        super(LoggerFactory.getLogger("categoriesLogger"),Map.of("name", "La categoría con el nombre '" + name + "' ya existe."));
    }
    
}
