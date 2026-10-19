package com.tfg.cultura.api.loans.factory;

import java.time.LocalDate;

import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;

public class LoanFactory {
    public static Loan boardGameLoan(String userId) {
        return Loan.builder()
                .id("boardGameLoanId")
                .code("ABC123")
                .status(LoanStatus.ACTIVE)
                .requestDate(LocalDate.of(2023, 1, 1))
                .startDate(LocalDate.of(2023, 1, 2))
                .dueDate(LocalDate.of(2023, 1, 10))
                .userId(userId)
                .itemId("loanedGameId")
                .build();
    }
}
