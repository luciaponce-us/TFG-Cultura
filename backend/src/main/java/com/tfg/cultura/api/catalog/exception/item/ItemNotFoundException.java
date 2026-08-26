package com.tfg.cultura.api.catalog.exception.item;

import org.slf4j.LoggerFactory;

import com.tfg.cultura.api.core.exception.NotFoundException;

public class ItemNotFoundException extends NotFoundException {
    public ItemNotFoundException(String message) {
        super(message, LoggerFactory.getLogger("catalogLogger"));
    }

}
