package com.tfg.cultura.api.catalog.exception;

public class SagaFieldsMustBeEmptyIfNotSagaException extends RuntimeException {
    public SagaFieldsMustBeEmptyIfNotSagaException(String message) {
        super(message);
    }

}
