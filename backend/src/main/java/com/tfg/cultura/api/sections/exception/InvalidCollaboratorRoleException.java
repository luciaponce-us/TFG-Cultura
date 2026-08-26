package com.tfg.cultura.api.sections.exception;

import java.util.Map;

import org.slf4j.LoggerFactory;

import com.tfg.cultura.api.core.exception.ValidationException;

public class InvalidCollaboratorRoleException extends ValidationException {
    public InvalidCollaboratorRoleException(String nonColaboradores) {
        super(
            LoggerFactory.getLogger("sectionsLogger"),
            Map.of("collaborators", "Los siguientes usuarios no son colaboradores: " + nonColaboradores)
        );
    }
    
}
