package com.tfg.cultura.api.catalog.exception.item;

import com.tfg.cultura.api.core.exception.DuplicationException;
import java.util.Map;
import org.slf4j.LoggerFactory;

public class ItemAlreadyExistsException extends DuplicationException {
	public ItemAlreadyExistsException(Map<String, String> errors) {
		super(LoggerFactory.getLogger("catalogLogger"), errors);
	}

}
