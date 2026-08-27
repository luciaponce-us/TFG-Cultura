package com.tfg.cultura.api.sections.exception;

import com.tfg.cultura.api.core.exception.FieldException;
import java.util.Map;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

public class CollaboratorAlreadyAssignedException extends FieldException {
	public CollaboratorAlreadyAssignedException(String alreadyAssignedCollaborators) {
		super(LoggerFactory.getLogger("sectionsLogger"), HttpStatus.CONFLICT,
				Map.of("collaborators",
						"Los siguientes usuarios ya están asignados como colaboradores de otras secciones: "
								+ alreadyAssignedCollaborators));
	}

}
