package com.tfg.cultura.api.catalog.exception.item;

public class ItemAlreadyExistsException extends RuntimeException {
    public ItemAlreadyExistsException(String message) {
        super(message);
    }

}
