package com.tfg.cultura.api.loans.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.core.exception.UnauthorizedException;
import com.tfg.cultura.api.core.factory.ExceptionsFactory;
import com.tfg.cultura.api.loans.factory.LoanFactory;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.dto.LoanCreateRequest;
import com.tfg.cultura.api.loans.model.dto.LoanResponse;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.loans.service.LoanCreateService;
import com.tfg.cultura.api.loans.service.LoanDeleteService;
import com.tfg.cultura.api.loans.service.LoanReadService;
import com.tfg.cultura.api.loans.service.LoanUpdateService;
import com.tfg.cultura.api.utils.BaseControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;

class LoanControllerTest extends BaseControllerTest {

	@Mock
	private LoanCreateService loanCreateService;

	@Mock
	private LoanReadService loanReadService;

	@Mock
	private LoanUpdateService loanUpdateService;

	@Mock
	private LoanDeleteService loanDeleteService;

	private static final String BASE_URL = "/api/loans";

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		LoanController controller = new LoanController(loanCreateService, loanReadService, loanUpdateService,
				loanDeleteService);
		mockMvc = buildMockMvc(controller);
	}

	@Nested
	@DisplayName("POST /api/loans - Create Loan")
	class CreateLoanTests {
		private LoanCreateRequest loanCreateRequest;
		private Loan loan;
		private LoanResponse loanResponse;

		@BeforeEach
		void setUp() {
			loan = LoanFactory.boardGameLoan("currentUserId");
			loanCreateRequest = LoanFactory.validLoanCreateRequest(loan.getItemType());
			loanResponse = new LoanResponse(loan, "currentUser");
		}

		// Positive test cases

		@Test
		void should_create_loan_successfully() throws Exception {
			when(loanCreateService.createLoan(any(LoanCreateRequest.class))).thenReturn(loanResponse);

			mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(toJson(loanCreateRequest)))
					.andExpect(status().isCreated()).andExpect(jsonPath("$.id").value(loanResponse.getId()))
					.andExpect(jsonPath("$.code").value(loanResponse.getCode()))
					.andExpect(jsonPath("$.status").value(loanResponse.getStatus().toString()))
					.andExpect(jsonPath("$.username").value(loanResponse.getUsername()))
					.andExpect(jsonPath("$.itemId").value(loanResponse.getItemId()));
		}

		// Negative test cases

		@Test
		void should_return_bad_request_when_create_loan_with_invalid_request() throws Exception {
			LoanCreateRequest invalidRequest = new LoanCreateRequest(); // Empty request

			mockMvc.perform(post(BASE_URL).contentType(MediaType.APPLICATION_JSON).content(toJson(invalidRequest)))
					.andExpect(status().isBadRequest());
		}

	}

	@Nested
	@DisplayName("GET /api/loans - Get All Loans")
	class GetAllLoansTests {
		private Page<LoanResponse> loanResponsePage;
		private Page<LoanResponse> emptyLoanResponsePage;
		private LoanResponse loanResponse1;
		private LoanResponse loanResponse2;

		@BeforeEach
		void setUp() {
			loanResponse1 = LoanFactory.boardGameLoanResponse("user1Id", "user1");
			loanResponse2 = LoanFactory.rolGameLoanResponse("user2Id", "user2");
			loanResponsePage = LoanFactory.loanResponsePage();
			emptyLoanResponsePage = LoanFactory.emptyLoanResponsePage();
		}

		// Positive test cases

		@Test
		void should_get_all_loans_successfully() throws Exception {
			when(loanReadService.getAllLoans(any(), any(), any())).thenReturn(loanResponsePage);

			mockMvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
					.andExpect(jsonPath("$.content").isArray())
					.andExpect(jsonPath("$.content[0].id").value(loanResponse1.getId()))
					.andExpect(jsonPath("$.content[1].id").value(loanResponse2.getId()));
		}

		@Test
		void should_return_empty_page_when_no_loans_exist() throws Exception {
			when(loanReadService.getAllLoans(any(), any(), any())).thenReturn(emptyLoanResponsePage);

			mockMvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
					.andExpect(jsonPath("$.content").isArray()).andExpect(jsonPath("$.content").isEmpty());
		}

		// Negative test cases

		@Test
		void should_return_bad_request_when_get_all_loans_with_invalid_page_parameters() throws Exception {
			mockMvc.perform(
					get(BASE_URL).param("page", "-1").param("size", "0").contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isBadRequest());
		}

		@Test
		void should_return_404_when_get_all_loans_with_nonexistent_username() throws Exception {
			when(loanReadService.getAllLoans(any(), any(), any())).thenReturn(emptyLoanResponsePage);

			mockMvc.perform(get(BASE_URL).param("username", "nonexistentUser").contentType(MediaType.APPLICATION_JSON))
					.andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray())
					.andExpect(jsonPath("$.content").isEmpty());
		}
	}

	@Nested
	@DisplayName("PUT /api/loans/{loanCode}/status - Update Loan Status")
	class UpdateLoanStatusTests {

		private static final String CHANGE_STATUS_URL = BASE_URL + "/{loanCode}/status";
		private LoanResponse loanResponse;

		@BeforeEach
		void setUp() {
			loanResponse = LoanFactory.boardGameLoanResponse("currentUserId", "currentUser");
		}

		@Test
		void should_update_loan_status_successfully() throws Exception {
			when(loanUpdateService.updateLoanStatus(any(), any())).thenReturn(loanResponse);

			mockMvc.perform(put(CHANGE_STATUS_URL, loanResponse.getCode())
					.contentType(MediaType.APPLICATION_JSON)
					.content(toJson(LoanStatus.CANCELLED)))
					.andExpect(status().isOk())
					.andExpect(jsonPath("$.id").value(loanResponse.getId()))
					.andExpect(jsonPath("$.code").value(loanResponse.getCode()))
					.andExpect(jsonPath("$.status").value(loanResponse.getStatus().toString()))
					.andExpect(jsonPath("$.username").value(loanResponse.getUsername()))
					.andExpect(jsonPath("$.itemId").value(loanResponse.getItemId()));
		}

		@Test
		void should_return_bad_request_when_update_loan_status_with_invalid_status() throws Exception {
			mockMvc.perform(put(CHANGE_STATUS_URL, loanResponse.getCode())
					.contentType(MediaType.APPLICATION_JSON)
					.content(toJson("INVALID_STATUS")))
					.andExpect(status().isBadRequest());
		}

		@Test
		void should_propagate_not_found_exception() throws Exception {
			when(loanUpdateService.updateLoanStatus(any(), any()))
					.thenThrow(ExceptionsFactory.notFoundException);

			mockMvc.perform(put(CHANGE_STATUS_URL, loanResponse.getCode())
					.contentType(MediaType.APPLICATION_JSON)
					.content(toJson(LoanStatus.CANCELLED)))
					.andExpect(status().isNotFound());
		}

		@Test
		void should_propagate_unauthorized_exception() throws Exception {
			when(loanUpdateService.updateLoanStatus(any(), any())).thenThrow(new UnauthorizedException(null));

			mockMvc.perform(put(CHANGE_STATUS_URL, loanResponse.getCode())
					.contentType(MediaType.APPLICATION_JSON)
					.content(toJson(LoanStatus.CANCELLED)))
					.andExpect(status().isForbidden());
		}

		@Test
		void should_propagate_unauthenticated_exception() throws Exception {
			when(loanUpdateService.updateLoanStatus(any(), any())).thenThrow(new UnathenticatedException());

			mockMvc.perform(put(CHANGE_STATUS_URL, loanResponse.getCode())
					.contentType(MediaType.APPLICATION_JSON)
					.content(toJson(LoanStatus.CANCELLED)))
					.andExpect(status().isUnauthorized());
		}

		@Test
		void should_propagate_validation_exception() throws Exception {
			when(loanUpdateService.updateLoanStatus(any(), any()))
					.thenThrow(ExceptionsFactory.validationException("Invalid status change"));

			mockMvc.perform(put(CHANGE_STATUS_URL, loanResponse.getCode())
					.contentType(MediaType.APPLICATION_JSON)
					.content(toJson(LoanStatus.CANCELLED)))
					.andExpect(status().isBadRequest());
		}
	}
}
