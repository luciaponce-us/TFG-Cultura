package com.tfg.cultura.api.loans.service.specifications;

import com.tfg.cultura.api.catalog.model.Item;
import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.core.exception.FieldException;

public interface LoanSpecificationInterface<T extends Item> {

	void validate(T item, String userId, ItemType itemType) throws FieldException;
}
