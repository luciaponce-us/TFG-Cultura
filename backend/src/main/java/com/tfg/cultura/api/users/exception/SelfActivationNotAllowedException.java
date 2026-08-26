package com.tfg.cultura.api.users.exception;

import com.tfg.cultura.api.core.exception.UnauthorizedException;

public class SelfActivationNotAllowedException extends UnauthorizedException {
    public SelfActivationNotAllowedException() {
        super("No puedes activar tu propio usuario");
    }
}
