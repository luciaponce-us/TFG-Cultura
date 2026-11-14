package com.tfg.cultura.api.loans.factory;

import static org.mockito.Mockito.when;

import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.dto.LoanCreateRequest;
import com.tfg.cultura.api.loans.model.dto.LoanResponse;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.model.User;
import java.time.LocalDate;
import java.util.List;
import org.mockito.ArgumentMatchers;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

public class LoanFactory {

	public static Loan validLoan(String ownerId, LoanStatus status) {
		return Loan.builder().id("loanId").code("ABC123").status(status).requestDate(LocalDate.of(2023, 1, 1))
				.userId(ownerId).itemId("loanedItemId").itemType(ItemType.BOARDGAME).build();
	}

	public static Loan boardGameLoan(String userId) {
		return Loan.builder().id("boardGameLoanId").code("ABC123").status(LoanStatus.ACTIVE)
				.requestDate(LocalDate.of(2023, 1, 1)).startDate(LocalDate.of(2023, 1, 2))
				.dueDate(LocalDate.of(2023, 1, 10)).userId(userId).itemId("loanedGameId").itemType(ItemType.BOARDGAME)
				.build();
	}

	public static LoanResponse boardGameLoanResponse(String userId, String username) {
		Loan loan = boardGameLoan(userId);
		return new LoanResponse(loan, username);
	}

	public static Loan rolGameLoan(String userId) {
		return Loan.builder().id("rolGameLoanId").code("XYZ789").status(LoanStatus.ACTIVE)
				.requestDate(LocalDate.of(2023, 2, 1)).startDate(LocalDate.of(2023, 2, 2))
				.dueDate(LocalDate.of(2023, 2, 10)).userId(userId).itemId("loanedRolGameId").itemType(ItemType.ROLGAME)
				.build();
	}

	public static LoanResponse rolGameLoanResponse(String userId, String username) {
		Loan loan = rolGameLoan(userId);
		return new LoanResponse(loan, username);
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

	public static Page<LoanResponse> loanResponsePage() {
		User user1 = UserFactory.validUser();
		User user2 = UserFactory.validUser2();
		LoanResponse loan1 = boardGameLoanResponse(user1.getId(), user1.getUsername());
		LoanResponse loan2 = rolGameLoanResponse(user2.getId(), user2.getUsername());
		return new PageImpl<>(List.of(loan1, loan2), PageRequest.of(0, 10), 2);
	}

	public static Page<Loan> emptyLoanPage() {
		return new PageImpl<Loan>(List.of());
	}

	public static Page<LoanResponse> emptyLoanResponsePage() {
		return new PageImpl<>(List.of(), PageRequest.of(0, 10), 0);
	}

	public static Page<Loan> loanPage(String userId, String itemId) {
		Loan loan = Loan.builder().id("loanId").code("ABC123").status(LoanStatus.ACTIVE)
				.requestDate(LocalDate.of(2023, 1, 1)).startDate(LocalDate.of(2023, 1, 2))
				.dueDate(LocalDate.of(2023, 1, 10)).userId(userId).itemId(itemId).itemType(ItemType.BOARDGAME).build();
		return new PageImpl<Loan>(List.of(loan));
	}

	public static void mockSuccessfullySavedLoan(LoanRepository loanRepository) {
		when(loanRepository.save(ArgumentMatchers.any())).thenAnswer(i -> i.getArguments()[0]);
	}

}
