package com.tfg.cultura.api.loans.service.specifications;

import com.tfg.cultura.api.catalog.model.Item;
import com.tfg.cultura.api.core.exception.FieldException;

public interface LoanSpecification<T extends Item> {

    void validate(T item, String userId) throws FieldException;
}
