package com.tfg.cultura.api.loans.service.specifications;

import com.tfg.cultura.api.catalog.model.Item;
import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.core.exception.FieldException;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.loans.repository.LoanRepository;

import java.util.Map;
import java.util.Set;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanSpecification<T extends Item> implements LoanSpecificationInterface<T> {

	protected static final Logger logger = LoggerFactory.getLogger("loansLogger");
	private final LoanRepository loanRepository;

	public void validate(T item, String userId, ItemType itemType) throws FieldException {
		validateAvailability(item);
		Set<Loan> activeLoans = getActiveLoans(userId, itemType);
		validateNoActiveLoans(activeLoans, itemType);
	}

	protected void validateAvailability(T item) throws FieldException {
		boolean isAvailable = item.getLoanAvailable();
		if (!isAvailable) {
			throw new FieldException(logger, HttpStatus.CONFLICT,
					Map.of("itemId", "El artículo no está disponible para préstamo."));
		}
	}

	protected Set<Loan> getActiveLoans(String userId, ItemType itemType) {
		return loanRepository.findByUserIdAndStatusAndItemType(userId, LoanStatus.ACTIVE, itemType);
	}

	protected void validateNoActiveLoans(Set<Loan> activeLoans, ItemType itemType) throws FieldException {
		if (!activeLoans.isEmpty()) {
			throw new FieldException(logger, HttpStatus.CONFLICT, Map.of("itemId", getErrorMessage(itemType)));
		}
	}

	private String getErrorMessage(ItemType itemType) {
		return switch (itemType) {
			case BOOK -> "No se puede solicitar un préstamo de varios libros concurrentemente.";
			case MOVIE -> "No se puede solicitar un préstamo de varias películas concurrentemente.";
			case SERIES -> "No se puede solicitar un préstamo de varias series concurrentemente.";
			default ->
				throw new IllegalArgumentException("El tipo de artículo no admite esta especificación: " + itemType);
		};
	}
}
