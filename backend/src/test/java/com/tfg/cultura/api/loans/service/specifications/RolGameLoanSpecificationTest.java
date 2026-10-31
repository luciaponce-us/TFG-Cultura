package com.tfg.cultura.api.loans.service.specifications;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.RolGame;
import com.tfg.cultura.api.catalog.model.RolSaga;
import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.catalog.service.RolGameService;
import com.tfg.cultura.api.core.exception.FieldException;
import com.tfg.cultura.api.loans.factory.LoanFactory;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RolGameLoanSpecificationTest {

	@Mock
	private LoanRepository loanRepository;

	@Mock
	private RolGameService rolGameService;

	@InjectMocks
	private RolGameLoanSpecification specification;

	private RolGame requestedGame;
	private RolSaga requestedSaga;
	private Loan activeLoan;
	private RolGame activeLoanedGame;
	private RolSaga activeLoanedSaga;

	@BeforeEach
	void setUp() {
		requestedSaga = CatalogFactory.validRolSagaWithId("requestedSagaId");
		requestedGame = CatalogFactory.validRolGameWithSaga("requestedGameId", requestedSaga);
		activeLoan = LoanFactory.rolGameLoan("userId");
		activeLoanedSaga = CatalogFactory.validRolSagaWithId("requestedSagaId"); // Same saga as requestedGame
		activeLoanedGame = CatalogFactory.validRolGameWithSaga("activeLoanedGameId", activeLoanedSaga);
	}

	@Test
	void should_validate_successfully_when_no_active_loans() {
		when(loanRepository.findByUserIdAndStatusAndItemType(anyString(), eq(LoanStatus.ACTIVE), eq(ItemType.ROLGAME)))
				.thenReturn(Set.of());

		specification.validate(requestedGame, "userId");
	}

	@Test
	void should_validate_successfully_when_active_loan_with_same_saga() {
		when(loanRepository.findByUserIdAndStatusAndItemType(anyString(), eq(LoanStatus.ACTIVE), eq(ItemType.ROLGAME)))
				.thenReturn(Set.of(activeLoan));
		when(rolGameService.findById(activeLoan.getItemId())).thenReturn(activeLoanedGame);

		specification.validate(requestedGame, "userId");
	}

	@Test
	void should_throw_field_exception_when_loan_unavailable() {
		requestedGame.setLoanAvailable(false);

		FieldException ex = assertThrows(FieldException.class, () -> {
			specification.validate(requestedGame, "userId");
		});

		assertTrue(ex.getErrors().containsKey("itemId"));
	}

	@Test
	void should_throw_field_exception_when_active_loan_with_different_saga() {
		activeLoanedSaga.setId("differentSagaId");
		activeLoanedGame.setSaga(activeLoanedSaga);

		when(loanRepository.findByUserIdAndStatusAndItemType(anyString(), eq(LoanStatus.ACTIVE), eq(ItemType.ROLGAME)))
				.thenReturn(Set.of(activeLoan));
		when(rolGameService.findById(activeLoan.getItemId())).thenReturn(activeLoanedGame);

		FieldException ex = assertThrows(FieldException.class, () -> {
			specification.validate(requestedGame, "userId");
		});

		assertTrue(ex.getErrors().containsKey("itemId"));
		assertTrue(ex.getErrors().get("itemId").contains("saga"));
	}
}
