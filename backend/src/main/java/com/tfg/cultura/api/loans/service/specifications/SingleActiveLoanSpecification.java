package com.tfg.cultura.api.loans.service.specifications;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.core.exception.FieldException;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.loans.repository.LoanRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class SingleActiveLoanSpecification {

    private static final Logger logger = LoggerFactory.getLogger("loansLogger");

    private final LoanRepository loanRepository;

    public void validate(String userId, ItemType itemType) throws FieldException {
        Set<Loan> activeLoans = loanRepository
                .findByUserIdAndStatusAndItemType(
                        userId,
                        LoanStatus.ACTIVE,
                        itemType
                );

        if (!activeLoans.isEmpty()) {
            throw new FieldException(
                    logger,
                    HttpStatus.CONFLICT,
                    Map.of(
                            "itemId",
                            getErrorMessage(itemType)
                    )
            );
        }
    }

    private String getErrorMessage(ItemType itemType) {
        return switch (itemType) {
            case BOOK ->
                "No se puede solicitar un préstamo de varios libros concurrentemente.";
            case MOVIE ->
                "No se puede solicitar un préstamo de varias películas concurrentemente.";
            case SERIES ->
                "No se puede solicitar un préstamo de varias series concurrentemente.";
            default ->
                throw new IllegalArgumentException(
                        "El tipo de artículo no admite esta especificación: " + itemType
                );
        };
    }
}
