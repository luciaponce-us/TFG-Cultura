package com.tfg.cultura.api.core.exception;

public class DateMustBeAtThePastException extends RuntimeException {
    public DateMustBeAtThePastException(String message) {
        super(message);
    }

}
