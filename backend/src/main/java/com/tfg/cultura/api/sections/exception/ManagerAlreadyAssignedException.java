package com.tfg.cultura.api.sections.exception;

import java.util.Map;

import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import com.tfg.cultura.api.core.exception.FieldException;

public class ManagerAlreadyAssignedException extends FieldException {
    public ManagerAlreadyAssignedException(String alreadyAssignedManagers) {
        super(
            LoggerFactory.getLogger("sectionsLogger"),
            HttpStatus.CONFLICT,
            Map.of("managers", "Los siguientes usuarios ya están asignados como gestores en otra sección: "
                            + alreadyAssignedManagers)
        );
    }
    
}
