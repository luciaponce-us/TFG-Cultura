package com.tfg.cultura.api.categories.exception;

public class CategoryAlreadyExistsException extends RuntimeException {
    public CategoryAlreadyExistsException(String name) {
        super("La categoría con el nombre '" + name + "' ya existe.");
    }
    
}
