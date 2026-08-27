package com.tfg.cultura.api.catalog.exception.rolsaga;

import com.tfg.cultura.api.core.exception.DuplicationException;
import java.util.Map;
import org.slf4j.LoggerFactory;

public class RolSagaAlreadyExistsException extends DuplicationException {
	public RolSagaAlreadyExistsException(String field, String message) {
		super(LoggerFactory.getLogger("catalogLogger"), Map.of(field, message));
	}

}
