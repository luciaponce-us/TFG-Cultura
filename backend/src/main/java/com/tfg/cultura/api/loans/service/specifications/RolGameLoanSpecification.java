package com.tfg.cultura.api.loans.service.specifications;

import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tfg.cultura.api.catalog.model.RolGame;
import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.catalog.service.RolGameService;
import com.tfg.cultura.api.core.exception.FieldException;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.loans.repository.LoanRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class RolGameLoanSpecification
        implements LoanSpecification<RolGame> {

    private static final Logger logger =
            LoggerFactory.getLogger("loansLogger");

    private final LoanRepository loanRepository;
    private final RolGameService rolGameService;

    @Override
    public void validate(RolGame requestedGame, String userId) throws FieldException {
        if (!requestedGame.getLoanAvailable()) {
            throw new FieldException(
                    logger,
                    HttpStatus.CONFLICT,
                    Map.of(
                            "itemId",
                            "El juego de rol no está disponible para préstamo"
                    )
            );
        }

        Set<Loan> activeLoans =
                loanRepository.findByUserIdAndStatusAndItemType(
                        userId,
                        LoanStatus.ACTIVE,
                        ItemType.ROLGAME
                );

        if (activeLoans.isEmpty()) {
            return;
        }

        String requestedSagaId = requestedGame.getSaga().getId();

        boolean differentSaga = activeLoans.stream()
                .map(loan -> rolGameService.findById(loan.getItemId()))
                .anyMatch(game ->
                        !game.getSaga().getId().equals(requestedSagaId)
                );

        if (differentSaga) {
            throw new FieldException(
                    logger,
                    HttpStatus.CONFLICT,
                    Map.of(
                            "itemId",
                            "No se puede solicitar un préstamo de un juego de rol de una saga diferente mientras se tenga un préstamo activo de otro juego de rol"
                    )
            );
        }
    }
}
