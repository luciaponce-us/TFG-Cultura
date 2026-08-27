package com.tfg.cultura.api.sections.exception;

import com.tfg.cultura.api.core.exception.ValidationException;
import java.util.Map;
import org.slf4j.LoggerFactory;

public class InvalidManagerRoleException extends ValidationException {
	public InvalidManagerRoleException(String nonEncargados) {
		super(LoggerFactory.getLogger("sectionsLogger"),
				Map.of("managers", "Los siguientes usuarios no son encargados: " + nonEncargados));
	}

}
