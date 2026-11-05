package com.tfg.cultura.api.loans.factory;

import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.dto.LoanCreateRequest;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.users.factory.UserFactory;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

public class LoanFactory {
	public static Loan boardGameLoan(String userId) {
		return Loan.builder().id("boardGameLoanId").code("ABC123").status(LoanStatus.ACTIVE)
				.requestDate(LocalDate.of(2023, 1, 1)).startDate(LocalDate.of(2023, 1, 2))
				.dueDate(LocalDate.of(2023, 1, 10)).userId(userId).itemId("loanedGameId").itemType(ItemType.BOARDGAME)
				.build();
	}

	public static Loan rolGameLoan(String userId) {
		return Loan.builder().id("rolGameLoanId").code("XYZ789").status(LoanStatus.ACTIVE)
				.requestDate(LocalDate.of(2023, 2, 1)).startDate(LocalDate.of(2023, 2, 2))
				.dueDate(LocalDate.of(2023, 2, 10)).userId(userId).itemId("loanedRolGameId").itemType(ItemType.ROLGAME)
				.build();
	}

	public static LoanCreateRequest validLoanCreateRequest(ItemType itemType) {
		return LoanCreateRequest.builder().itemId("loanedItemId").itemType(itemType).build();
	}

	public static Page<Loan> loanPage() {
		String user1Id = UserFactory.validUser().getId();
		String user2Id = UserFactory.validUser2().getId();
		Loan loan1 = boardGameLoan(user1Id);
		Loan loan2 = rolGameLoan(user2Id);
		return new PageImpl<Loan>(List.of(loan1, loan2));
	}

	public static Page<Loan> emptyLoanPage() {
		return new PageImpl<Loan>(List.of());
	}

	public static Page<Loan> loanPage(String userId, String itemId) {
		Loan loan = Loan.builder().id("loanId").code("ABC123").status(LoanStatus.ACTIVE)
				.requestDate(LocalDate.of(2023, 1, 1)).startDate(LocalDate.of(2023, 1, 2))
				.dueDate(LocalDate.of(2023, 1, 10)).userId(userId).itemId(itemId).itemType(ItemType.BOARDGAME)
				.build();
		return new PageImpl<Loan>(List.of(loan));
	}

}
