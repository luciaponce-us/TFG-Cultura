package com.tfg.cultura.api.loans.service.specifications;

import com.tfg.cultura.api.catalog.model.Item;
import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.core.exception.FieldException;
import java.util.Map;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
public abstract class AbstractLoanSpecification<T extends Item>
        implements LoanSpecification<T> {

	private final Logger logger;

    protected void validateAvailability(T item, ItemType itemType) throws FieldException {
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
