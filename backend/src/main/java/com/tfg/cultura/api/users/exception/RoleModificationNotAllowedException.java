package com.tfg.cultura.api.users.exception;

public class RoleModificationNotAllowedException extends RuntimeException {
    public RoleModificationNotAllowedException(String message) {
        super(message);
    }
}
