package com.tfg.cultura.api.catalog.exception.item;

import com.tfg.cultura.api.core.exception.NotFoundException;
import org.slf4j.LoggerFactory;

public class ItemNotFoundException extends NotFoundException {
	public ItemNotFoundException(String message) {
		super(message, LoggerFactory.getLogger("catalogLogger"));
	}

}
