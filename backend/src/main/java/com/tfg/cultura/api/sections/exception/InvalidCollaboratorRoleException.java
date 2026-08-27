package com.tfg.cultura.api.sections.exception;

import com.tfg.cultura.api.core.exception.ValidationException;
import java.util.Map;
import org.slf4j.LoggerFactory;

public class InvalidCollaboratorRoleException extends ValidationException {
	public InvalidCollaboratorRoleException(String nonColaboradores) {
		super(LoggerFactory.getLogger("sectionsLogger"),
				Map.of("collaborators", "Los siguientes usuarios no son colaboradores: " + nonColaboradores));
	}

}
