package com.tfg.cultura.api.sections.exception;

public class ManagerAlreadyAssignedException extends RuntimeException {
    public ManagerAlreadyAssignedException(String message) {
        super(message);
    }
    
}
