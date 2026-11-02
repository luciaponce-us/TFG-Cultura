package com.tfg.cultura.api.loans.factory;

import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.dto.LoanCreateRequest;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import java.time.LocalDate;

public class LoanFactory {
	public static Loan boardGameLoan(String userId) {
		return Loan.builder().id("boardGameLoanId").code("ABC123").status(LoanStatus.ACTIVE)
				.requestDate(LocalDate.of(2023, 1, 1)).startDate(LocalDate.of(2023, 1, 2))
				.dueDate(LocalDate.of(2023, 1, 10)).userId(userId).itemId("loanedGameId").build();
	}

	public static Loan rolGameLoan(String userId) {
		return Loan.builder().id("rolGameLoanId").code("XYZ789").status(LoanStatus.ACTIVE)
				.requestDate(LocalDate.of(2023, 2, 1)).startDate(LocalDate.of(2023, 2, 2))
				.dueDate(LocalDate.of(2023, 2, 10)).userId(userId).itemId("loanedRolGameId").build();
	}

	public static LoanCreateRequest validLoanCreateRequest(ItemType itemType) {
		return LoanCreateRequest.builder()
		.itemId("loanedItemId")
		.itemType(itemType)
		.build();
	}
}
