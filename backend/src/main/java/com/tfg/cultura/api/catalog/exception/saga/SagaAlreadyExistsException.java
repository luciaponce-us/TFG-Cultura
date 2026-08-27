package com.tfg.cultura.api.catalog.exception.saga;

import com.tfg.cultura.api.core.exception.DuplicationException;
import java.util.Map;
import org.slf4j.LoggerFactory;

public class SagaAlreadyExistsException extends DuplicationException {
	public SagaAlreadyExistsException(String name) {
		super(LoggerFactory.getLogger("catalogLogger"), Map.of("name", "Ya existe una saga con el nombre: " + name));
	}
}