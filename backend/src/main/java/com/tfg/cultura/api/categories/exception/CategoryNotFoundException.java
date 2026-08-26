package com.tfg.cultura.api.categories.exception;

import org.slf4j.LoggerFactory;

import com.tfg.cultura.api.core.exception.NotFoundException;

public class CategoryNotFoundException extends NotFoundException {
    public CategoryNotFoundException(String message) {
        super(message, LoggerFactory.getLogger("categoriesLogger"));
    }
    
}
