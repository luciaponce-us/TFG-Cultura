package com.tfg.cultura.api.catalog.exception;

public class DuplicateItemNumberInSagaException extends RuntimeException {
    public DuplicateItemNumberInSagaException(String message) {
        super(message);
    }

}
