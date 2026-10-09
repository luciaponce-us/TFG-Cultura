package com.tfg.cultura.api.loans.service.specifications;

import java.util.Map;

import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import com.tfg.cultura.api.catalog.model.Item;
import com.tfg.cultura.api.core.exception.FieldException;

public abstract class AbstractLoanSpecification<T extends Item>
        implements LoanSpecification<T> {

    private final Logger logger;

    protected AbstractLoanSpecification(Logger logger) {
        this.logger = logger;
    }

    protected void validateAvailability(T item, String itemType) throws FieldException {
        if (!item.getLoanAvailable()) {
            throw new FieldException(
                    logger,
                    HttpStatus.CONFLICT,
                    Map.of(
                            "itemId",
                            "El artículo no está disponible para préstamo."
                    )
            );
        }
    }
}
