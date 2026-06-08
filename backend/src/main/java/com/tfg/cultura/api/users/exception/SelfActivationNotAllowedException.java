package com.tfg.cultura.api.users.exception;

public class SelfActivationNotAllowedException extends RuntimeException {
    public SelfActivationNotAllowedException(String message) {
        super(message);
    }
}
