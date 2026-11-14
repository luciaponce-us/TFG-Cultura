package com.tfg.cultura.api.loans.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.tfg.cultura.api.core.exception.NotFoundException;
import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.core.exception.UnauthorizedException;
import com.tfg.cultura.api.core.factory.ExceptionsFactory;
import com.tfg.cultura.api.loans.factory.LoanFactory;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.dto.LoanResponse;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.model.User;
import com.tfg.cultura.api.users.service.UserService;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

@ExtendWith(MockitoExtension.class)
class LoanReadServiceTest {

	@Mock
	private LoanRepository loanRepository;

	@Mock
	private CustomUserDetailsService userDetailsService;

	@Mock
	private UserService userService;

	@InjectMocks
	private LoanReadService loanService;

	// HELPERS

	private void mockLoggedUser() {
		UserFactory.mockLoggedUser(userDetailsService);
	}

	private final PageRequest defaultPageable = PageRequest.of(0, 10);

	@Nested
	@DisplayName("Get all loans")
	class GetAllLoansTests {

		// Positive test cases

		@Test
		void should_get_all_loans_without_filters_successfully() throws Exception {
			when(loanRepository.findAll(defaultPageable)).thenReturn(LoanFactory.loanPage());
			when(userService.findUserById(UserFactory.validUser().getId())).thenReturn(UserFactory.validUser());
			when(userService.findUserById(UserFactory.validUser2().getId())).thenReturn(UserFactory.validUser2());
			Page<LoanResponse> response = loanService.getAllLoans(defaultPageable, null, null);

			assertNotNull(response);
			assertEquals(2, response.getTotalElements());
		}

		@Test
		void should_get_all_loans_filtered_by_username_successfully() throws Exception {
			User user = UserFactory.validUser();
			String username = user.getUsername();
			String userId = user.getId();
			when(userService.findUserById(userId)).thenReturn(user);
			when(userService.findUserByUsername(username)).thenReturn(user);
			when(loanRepository.findByUserId(userId, defaultPageable))
					.thenReturn(LoanFactory.loanPage(userId, "randomItemId"));

			Page<LoanResponse> response = loanService.getAllLoans(defaultPageable, username, null);
			assertNotNull(response);
			assertEquals(1, response.getTotalElements());
		}

		@Test
		void should_get_all_loans_filtered_by_itemId_successfully() throws Exception {
			String itemId = "randomItemId";
			when(userService.findUserById(anyString())).thenReturn(UserFactory.validUser());
			when(loanRepository.findByItemId(itemId, defaultPageable))
					.thenReturn(LoanFactory.loanPage("randomUserId", itemId));

			Page<LoanResponse> response = loanService.getAllLoans(defaultPageable, null, itemId);
			assertNotNull(response);
			assertEquals(1, response.getTotalElements());
		}

		@Test
		void should_get_all_loans_filtered_by_username_and_itemId_successfully() throws Exception {
			User user = UserFactory.validUser();
			String username = user.getUsername();
			String userId = user.getId();
			String itemId = "randomItemId";
			when(userService.findUserById(userId)).thenReturn(user);
			when(userService.findUserByUsername(username)).thenReturn(user);
			when(loanRepository.findByUserIdAndItemId(userId, itemId, defaultPageable))
					.thenReturn(LoanFactory.loanPage(userId, itemId));

			Page<LoanResponse> response = loanService.getAllLoans(defaultPageable, username, itemId);
			assertNotNull(response);
			assertEquals(1, response.getTotalElements());
		}

		@Test
		void should_clean_filters_when_username_and_itemId_are_empty_strings() throws Exception {
			when(loanRepository.findAll(defaultPageable)).thenReturn(LoanFactory.loanPage());
			when(userService.findUserById(UserFactory.validUser().getId())).thenReturn(UserFactory.validUser());
			when(userService.findUserById(UserFactory.validUser2().getId())).thenReturn(UserFactory.validUser2());
			Page<LoanResponse> response = loanService.getAllLoans(defaultPageable, "", "");
			assertNotNull(response);
			assertEquals(2, response.getTotalElements());
		}

		@Test
		void should_return_empty_page_when_no_loans_found() throws Exception {
			when(loanRepository.findAll(defaultPageable)).thenReturn(LoanFactory.emptyLoanPage());
			Page<LoanResponse> result = loanService.getAllLoans(defaultPageable, null, null);
			assertTrue(result.isEmpty());
		}

		@Test
		void should_return_empty_page_when_no_loans_found_filtered_by_username() throws Exception {
			User user = UserFactory.validUser();
			String username = user.getUsername();
			String userId = user.getId();
			when(userService.findUserByUsername(username)).thenReturn(user);
			when(loanRepository.findByUserId(userId, defaultPageable)).thenReturn(LoanFactory.emptyLoanPage());
			Page<LoanResponse> result = loanService.getAllLoans(defaultPageable, username, null);
			assertTrue(result.isEmpty());
		}

		@Test
		void should_return_empty_page_when_no_loans_found_filtered_by_itemId() throws Exception {
			String itemId = "randomItemId";
			when(loanRepository.findByItemId(itemId, defaultPageable)).thenReturn(LoanFactory.emptyLoanPage());
			Page<LoanResponse> result = loanService.getAllLoans(defaultPageable, null, itemId);
			assertTrue(result.isEmpty());
		}

		@Test
		void should_return_empty_page_when_no_loans_found_filtered_by_username_and_itemId() throws Exception {
			User user = UserFactory.validUser();
			String username = user.getUsername();
			String userId = user.getId();
			String itemId = "randomItemId";
			when(userService.findUserByUsername(username)).thenReturn(user);
			when(loanRepository.findByUserIdAndItemId(userId, itemId, defaultPageable))
					.thenReturn(LoanFactory.emptyLoanPage());
			Page<LoanResponse> result = loanService.getAllLoans(defaultPageable, username, itemId);
			assertTrue(result.isEmpty());
		}

		// Negative test cases

		@Test
		void should_throw_not_found_exception_when_getting_all_loans_filtered_by_username_and_user_not_found()
				throws Exception {
			String username = "nonexistentUser";
			when(userService.findUserByUsername(username)).thenThrow(ExceptionsFactory.notFoundException);

			assertThrows(NotFoundException.class, () -> loanService.getAllLoans(defaultPageable, username, null));
		}
	}

	@Nested
	@DisplayName("Get loan by code")
	class GetLoanByCodeTests {

		private void mockLoggedAdmin() {
			when(userService.currentUserIsAdmin()).thenReturn(true);
		}

		// Positive test cases

		@Test
		void should_return_loan_by_code_when_current_user_is_owner() throws Exception {
			mockLoggedUser();
			Loan loan = LoanFactory.boardGameLoan("currentUserId");
			String loanCode = loan.getCode();
			when(loanRepository.findByCode(loanCode)).thenReturn(Optional.of(loan));
			when(userService.findUserById(loan.getUserId())).thenReturn(UserFactory.validUser());

			LoanResponse result = loanService.getLoanByCode(loanCode);

			assertNotNull(result);
			assertNotNull(result.getCode());
		}

		@Test
		void should_return_loan_by_code_when_current_user_is_admin() throws Exception {
			mockLoggedAdmin();
			Loan loan = LoanFactory.boardGameLoan("otherUserId");
			String loanCode = loan.getCode();
			when(loanRepository.findByCode(loanCode)).thenReturn(Optional.of(loan));
			when(userService.findUserById(loan.getUserId())).thenReturn(UserFactory.validUser());

			LoanResponse result = loanService.getLoanByCode(loanCode);

			assertNotNull(result);
			assertNotNull(result.getCode());
		}

		// Negative test cases

		@Test
		void should_throw_not_found_exception_when_loan_code_does_not_exist() throws Exception {
			String loanCode = "nonexistentCode";
			when(loanRepository.findByCode(loanCode)).thenReturn(Optional.empty());

			NotFoundException ex = assertThrows(NotFoundException.class, () -> loanService.getLoanByCode(loanCode));
			assertTrue(ex.getMessage().contains("préstamo"));
		}

		@Test
		void should_throw_unauthorized_exception_when_current_user_is_not_owner_nor_admin() throws Exception {
			mockLoggedUser();
			Loan loan = LoanFactory.boardGameLoan("otherUserId");
			String loanCode = loan.getCode();
			when(loanRepository.findByCode(loanCode)).thenReturn(Optional.of(loan));

			UnauthorizedException ex = assertThrows(UnauthorizedException.class,
					() -> loanService.getLoanByCode(loanCode));
			assertTrue(ex.getMessage().contains("préstamo"));
		}

		@Test
		void should_propagate_unathenticated_exception_when_current_user_is_not_authenticated() throws Exception {
			Loan loan = LoanFactory.boardGameLoan("otherUserId");
			String loanCode = loan.getCode();
			when(loanRepository.findByCode(loanCode)).thenReturn(Optional.of(loan));
			doThrow(new UnathenticatedException()).when(userDetailsService).getCurrentUserDetails();

			assertThrows(UnathenticatedException.class, () -> loanService.getLoanByCode(loanCode));
		}
	}

	@Nested
	@DisplayName("Get my loans")
	class GetMyLoansTests {
		// Positive test cases

		@Test
		void should_get_my_loans_successfully() throws Exception {
			mockLoggedUser();
			when(loanRepository.findByUserId("currentUserId", defaultPageable)).thenReturn(LoanFactory.loanPage());
			when(userService.findUserById(UserFactory.validUser().getId())).thenReturn(UserFactory.validUser());
			when(userService.findUserById(UserFactory.validUser2().getId())).thenReturn(UserFactory.validUser2());

			Page<LoanResponse> result = loanService.getMyLoans(defaultPageable);

			assertNotNull(result);
			assertEquals(2, result.getTotalElements());
		}

		// Negative test cases

		@Test
		void should_propagate_unathenticated_exception_when_getting_my_loans_and_current_user_is_not_authenticated()
				throws Exception {
			doThrow(new UnathenticatedException()).when(userDetailsService).getCurrentUserDetails();

			assertThrows(UnathenticatedException.class, () -> loanService.getMyLoans(defaultPageable));
		}
	}

}
