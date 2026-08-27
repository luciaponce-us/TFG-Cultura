package com.tfg.cultura.api.sections.exception;

import com.tfg.cultura.api.core.exception.DuplicationException;
import java.util.Map;
import org.slf4j.LoggerFactory;

public class SectionAlreadyExistsException extends DuplicationException {
	public SectionAlreadyExistsException(String name) {
		super(LoggerFactory.getLogger("sectionsLogger"),
				Map.of("name", "La sección con nombre " + name + " ya existe"));
	}

}
