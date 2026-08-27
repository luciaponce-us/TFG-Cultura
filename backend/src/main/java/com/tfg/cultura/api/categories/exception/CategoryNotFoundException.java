package com.tfg.cultura.api.categories.exception;

import com.tfg.cultura.api.core.exception.NotFoundException;
import org.slf4j.LoggerFactory;

public class CategoryNotFoundException extends NotFoundException {
	public CategoryNotFoundException(String message) {
		super(message, LoggerFactory.getLogger("categoriesLogger"));
	}

}
