package com.tfg.cultura.api.loans.service;

import static com.tfg.cultura.api.loans.factory.LoanFactory.mockSuccessfullySavedLoan;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.service.BoardGameService;
import com.tfg.cultura.api.catalog.service.BookService;
import com.tfg.cultura.api.catalog.service.MovieService;
import com.tfg.cultura.api.catalog.service.RolGameService;
import com.tfg.cultura.api.catalog.service.SeriesService;
import com.tfg.cultura.api.core.exception.NotFoundException;
import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.core.exception.UnauthorizedException;
import com.tfg.cultura.api.core.exception.ValidationException;
import com.tfg.cultura.api.loans.factory.LoanFactory;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.service.UserService;
import java.time.LocalDate;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanUpdateServiceTest {

	@Mock
	private RolGameService rolGameService;

	@Mock
	private BoardGameService boardGameService;
	@Mock
	private BookService bookService;
	@Mock
	private MovieService movieService;
	@Mock
	private SeriesService seriesService;

	@Mock
	private LoanRepository loanRepository;

	@Mock
	private CustomUserDetailsService userDetailsService;

	@Mock
	private UserService userService;

	@InjectMocks
	private LoanUpdateService loanUpdateService;

	private Loan loan;

	@BeforeEach
	void setUp() {
		loan = LoanFactory.validLoan("currentUserId", LoanStatus.REQUESTED);
	}

	private void mockLoggedUser() {
		UserFactory.mockLoggedUser(userDetailsService); // id = "currentUserId"
		when(userService.currentUserIsAdmin()).thenReturn(false);
	}

	// Negative test cases

	@Test
	void should_throw_NotFoundException_when_loan_not_found() {
		doThrow(NotFoundException.class).when(loanRepository).findByCode(anyString());

		assertThrows(NotFoundException.class, () -> {
			loanUpdateService.updateLoanStatus("nonExistentLoanCode", LoanStatus.ACTIVE);
		});

		verify(loanRepository).findByCode("nonExistentLoanCode");
		verify(loanRepository, never()).save(any(Loan.class));
	}

	@Test
	void should_throw_UnathenticatedException_when_user_not_authenticated() {
		when(loanRepository.findByCode(anyString())).thenReturn(Optional.of(loan));
		doThrow(UnathenticatedException.class).when(userService).currentUserIsAdmin();

		assertThrows(UnathenticatedException.class, () -> {
			loanUpdateService.updateLoanStatus(loan.getCode(), LoanStatus.ACTIVE);
		});

		verify(loanRepository, never()).save(any(Loan.class));
	}

	@Test
	void should_throw_UnauthorizedException_when_user_not_admin_and_not_loan_owner() {
		loan.setUserId("otherUserId");
		assertNotEquals(loan.getUserId(), "currentUserId");
		when(loanRepository.findByCode(anyString())).thenReturn(Optional.of(loan));
		mockLoggedUser();

		assertThrows(UnauthorizedException.class, () -> {
			loanUpdateService.updateLoanStatus(loan.getCode(), LoanStatus.ACTIVE);
		});

		verify(loanRepository).findByCode(loan.getCode());
		verify(loanRepository, never()).save(any(Loan.class));
	}

	@Nested
	@DisplayName("Loan status update positive tests")
	class LoanStatusUpdatePositiveTests {
		private void mockAdminUser() {
			UserFactory.mockLoggedUserAdmin(userDetailsService); // id = "currentUserId"
			when(userService.currentUserIsAdmin()).thenReturn(true);
		}

		private void mockLoan(boolean isOwner, LoanStatus status, boolean isAdmin) {
			String ownerId = isOwner ? "currentUserId" : "otherUserId";
			loan = LoanFactory.validLoan(ownerId, status);
			when(loanRepository.findByCode(anyString())).thenReturn(Optional.of(loan));
			if (isAdmin) {
				mockAdminUser();
			} else {
				mockLoggedUser();
			}
		}

		private void mockSuccessSaveLoan(boolean isOwner, LoanStatus status, boolean isAdmin) {
			mockLoan(isOwner, status, isAdmin);
			if (status == LoanStatus.REQUESTED) {
				when(boardGameService.findById(loan.getItemId())).thenReturn(CatalogFactory.validBoardGame());
			}
			String ownerId = isOwner ? "currentUserId" : "otherUserId";

			when(userService.findUserById(loan.getUserId())).thenReturn(UserFactory.validUserWithId(ownerId));
			mockSuccessfullySavedLoan(loanRepository);
		}

		@Test
		void should_update_loan_status_when_user_is_admin_and_not_loan_owner() {
			mockSuccessSaveLoan(false, LoanStatus.REQUESTED, true);
			loanUpdateService.updateLoanStatus(loan.getCode(), LoanStatus.ACTIVE);

			assertEquals(LoanStatus.ACTIVE, loan.getStatus());

			verify(loanRepository).findByCode(loan.getCode());
			verify(loanRepository).save(any(Loan.class));
		}

		@Test
		void should_update_loan_from_requested_to_cancelled_when_user_is_loan_owner() {
			mockSuccessSaveLoan(true, LoanStatus.REQUESTED, false);
			loanUpdateService.updateLoanStatus(loan.getCode(), LoanStatus.CANCELLED);

			assertEquals(LoanStatus.CANCELLED, loan.getStatus());
			assertNotNull(loan.getRequestDate());
			assertNotNull(loan.getCancelDate());

			verify(loanRepository).findByCode(loan.getCode());
			verify(loanRepository).save(any(Loan.class));
		}

		@Test
		void should_update_loan_from_requested_to_rejected_when_user_is_admin() {
			mockSuccessSaveLoan(false, LoanStatus.REQUESTED, true);
			loanUpdateService.updateLoanStatus(loan.getCode(), LoanStatus.REJECTED);

			assertEquals(LoanStatus.REJECTED, loan.getStatus());
			assertNotNull(loan.getRequestDate());
			assertNotNull(loan.getRejectionDate());

			verify(loanRepository).findByCode(loan.getCode());
			verify(loanRepository).save(any(Loan.class));
		}

		@Test
		void should_update_loan_from_requested_to_active_when_user_is_admin() {
			mockSuccessSaveLoan(false, LoanStatus.REQUESTED, true);
			loanUpdateService.updateLoanStatus(loan.getCode(), LoanStatus.ACTIVE);

			assertEquals(LoanStatus.ACTIVE, loan.getStatus());
			assertNotNull(loan.getRequestDate());
			assertNotNull(loan.getStartDate());
			LocalDate expectedDueDate = loan.getStartDate().plusDays(CatalogFactory.validBoardGame().getLoanDays());
			assertEquals(expectedDueDate, loan.getDueDate());

			verify(loanRepository).findByCode(loan.getCode());
			verify(loanRepository).save(any(Loan.class));
		}

		@Test
		void should_update_loan_from_active_to_returned_when_user_is_admin() {
			mockSuccessSaveLoan(false, LoanStatus.ACTIVE, true);
			loanUpdateService.updateLoanStatus(loan.getCode(), LoanStatus.RETURNED);

			assertEquals(LoanStatus.RETURNED, loan.getStatus());
			assertNotNull(loan.getRequestDate());
			assertNotNull(loan.getReturnDate());

			verify(loanRepository).findByCode(loan.getCode());
			verify(loanRepository).save(any(Loan.class));
		}

		@Test
		void should_update_loan_from_overdue_to_returned_when_user_is_admin() {
			mockSuccessSaveLoan(false, LoanStatus.OVERDUE, true);
			loanUpdateService.updateLoanStatus(loan.getCode(), LoanStatus.RETURNED);

			assertEquals(LoanStatus.RETURNED, loan.getStatus());
			assertNotNull(loan.getRequestDate());
			assertNotNull(loan.getReturnDate());

			verify(loanRepository).findByCode(loan.getCode());
			verify(loanRepository).save(any(Loan.class));
		}
	}

	@Nested
	@DisplayName("Loan status update negative tests")
	class LoanStatusUpdateNegativeTests {

		private void mockInvalidStatusChange(boolean isOwner, LoanStatus currentStatus, LoanStatus newStatus,
				boolean isAdmin) {
			String ownerId = isOwner ? "currentUserId" : "otherUserId";
			loan = LoanFactory.validLoan(ownerId, currentStatus);

			when(loanRepository.findByCode(anyString())).thenReturn(Optional.of(loan));
			when(userService.currentUserIsAdmin()).thenReturn(isAdmin);
			if (!isAdmin) {
				mockLoggedUser();
			}
		}

		private ValidationException invalidStatusChange(boolean isOwner, LoanStatus currentStatus, LoanStatus newStatus,
				boolean isAdmin) {
			mockInvalidStatusChange(isOwner, currentStatus, newStatus, isAdmin);
			return assertThrows(ValidationException.class, () -> {
				loanUpdateService.updateLoanStatus(loan.getCode(), newStatus);
			});
		}

		@Test
		void should_throw_UnauthorizedException_when_change_from_status_different_than_requested_and_user_is_not_admin() {
			mockInvalidStatusChange(true, LoanStatus.CANCELLED, LoanStatus.ACTIVE, false);
			UnauthorizedException ex = assertThrows(UnauthorizedException.class, () -> {
				loanUpdateService.updateLoanStatus(loan.getCode(), LoanStatus.ACTIVE);
			});
			assertTrue(ex.getMessage().contains("préstamo"));
		}

		@Test
		void should_throw_ValidationException_when_change_from_requested_to_invalid_status_and_user_is_not_admin() {
			ValidationException ex = invalidStatusChange(true, LoanStatus.REQUESTED, LoanStatus.ACTIVE, false);

			assertTrue(ex.getErrors().containsKey("status"));
		}

		@Test
		void should_throw_ValidationException_when_change_from_cancelled() {
			ValidationException ex = invalidStatusChange(true, LoanStatus.CANCELLED, LoanStatus.ACTIVE, true);

			assertTrue(ex.getErrors().containsKey("status"));
		}

		@Test
		void should_throw_ValidationException_when_change_from_rejected() {
			ValidationException ex = invalidStatusChange(true, LoanStatus.REJECTED, LoanStatus.ACTIVE, true);

			assertTrue(ex.getErrors().containsKey("status"));
		}

		@Test
		void should_throw_ValidationException_when_change_from_returned() {
			ValidationException ex = invalidStatusChange(true, LoanStatus.RETURNED, LoanStatus.ACTIVE, true);

			assertTrue(ex.getErrors().containsKey("status"));
		}

		@Test
		void should_throw_ValidationException_when_change_from_requested_to_invalid_status_and_user_is_admin() {
			ValidationException ex1 = invalidStatusChange(true, LoanStatus.REQUESTED, LoanStatus.CANCELLED, true);
			ValidationException ex2 = invalidStatusChange(true, LoanStatus.REQUESTED, LoanStatus.OVERDUE, true);
			ValidationException ex3 = invalidStatusChange(true, LoanStatus.REQUESTED, LoanStatus.RETURNED, true);
			ValidationException ex4 = invalidStatusChange(true, LoanStatus.REQUESTED, LoanStatus.REQUESTED, true);
			assertTrue(ex1.getErrors().containsKey("status"));
			assertTrue(ex2.getErrors().containsKey("status"));
			assertTrue(ex3.getErrors().containsKey("status"));
			assertTrue(ex4.getErrors().containsKey("status"));
		}

		@Test
		void should_throw_ValidationException_when_change_from_active_to_invalid_status_and_user_is_admin() {
			ValidationException ex1 = invalidStatusChange(true, LoanStatus.ACTIVE, LoanStatus.CANCELLED, true);
			ValidationException ex2 = invalidStatusChange(true, LoanStatus.ACTIVE, LoanStatus.OVERDUE, true);
			ValidationException ex3 = invalidStatusChange(true, LoanStatus.ACTIVE, LoanStatus.REQUESTED, true);
			ValidationException ex4 = invalidStatusChange(true, LoanStatus.ACTIVE, LoanStatus.REJECTED, true);
			ValidationException ex5 = invalidStatusChange(true, LoanStatus.ACTIVE, LoanStatus.ACTIVE, true);
			assertTrue(ex1.getErrors().containsKey("status"));
			assertTrue(ex2.getErrors().containsKey("status"));
			assertTrue(ex3.getErrors().containsKey("status"));
			assertTrue(ex4.getErrors().containsKey("status"));
			assertTrue(ex5.getErrors().containsKey("status"));
		}

		@Test
		void should_throw_ValidationException_when_change_from_overdue_to_invalid_status_and_user_is_admin() {
			ValidationException ex1 = invalidStatusChange(true, LoanStatus.OVERDUE, LoanStatus.CANCELLED, true);
			ValidationException ex2 = invalidStatusChange(true, LoanStatus.OVERDUE, LoanStatus.ACTIVE, true);
			ValidationException ex3 = invalidStatusChange(true, LoanStatus.OVERDUE, LoanStatus.REQUESTED, true);
			ValidationException ex4 = invalidStatusChange(true, LoanStatus.OVERDUE, LoanStatus.REJECTED, true);
			ValidationException ex5 = invalidStatusChange(true, LoanStatus.OVERDUE, LoanStatus.OVERDUE, true);
			assertTrue(ex1.getErrors().containsKey("status"));
			assertTrue(ex2.getErrors().containsKey("status"));
			assertTrue(ex3.getErrors().containsKey("status"));
			assertTrue(ex4.getErrors().containsKey("status"));
			assertTrue(ex5.getErrors().containsKey("status"));
		}
	}
}
