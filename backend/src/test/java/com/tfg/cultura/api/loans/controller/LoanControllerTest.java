package com.tfg.cultura.api.loans.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tfg.cultura.api.loans.factory.LoanFactory;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.dto.LoanCreateRequest;
import com.tfg.cultura.api.loans.model.dto.LoanResponse;
import com.tfg.cultura.api.loans.service.LoanService;
import com.tfg.cultura.api.utils.BaseControllerTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

class LoanControllerTest extends BaseControllerTest {

	@Mock
	private LoanService loanService;

	private static final String BASE_URL = "/api/loans";

	private Loan loan;
	private LoanCreateRequest loanCreateRequest;
	private LoanResponse loanResponse;

	@BeforeEach
	void setUp() {
		MockitoAnnotations.openMocks(this);
		LoanController controller = new LoanController(loanService);
		mockMvc = buildMockMvc(controller);
		initTestData();
	}

	private void initTestData() {
		loan = LoanFactory.boardGameLoan("currentUserId");
		loanCreateRequest = LoanFactory.validLoanCreateRequest(loan.getItemType());
		loanResponse = new LoanResponse(loan, "currentUser");
	}

	// CREATE LOAN

	// Positive test cases

	@Test
	void should_create_loan_successfully() throws Exception {
		when(loanService.createLoan(any(LoanCreateRequest.class))).thenReturn(loanResponse);

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

	// READ

	// Positive test cases

	@Test
	void should_get_all_loans_successfully() throws Exception {
		when(loanService.getAllLoans(any(), any(), any())).thenReturn(LoanFactory.loanResponsePage());

		mockMvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content[0].id").value(LoanFactory.boardGameLoanResponse("user1Id", "user1").getId()))
				.andExpect(jsonPath("$.content[1].id").value(LoanFactory.rolGameLoanResponse("user2Id", "user2").getId()));
	}

	@Test
	void should_return_empty_page_when_no_loans_exist() throws Exception {
		when(loanService.getAllLoans(any(), any(), any())).thenReturn(LoanFactory.emptyLoanResponsePage());

		mockMvc.perform(get(BASE_URL).contentType(MediaType.APPLICATION_JSON)).andExpect(status().isOk())
				.andExpect(jsonPath("$.content").isArray()).andExpect(jsonPath("$.content").isEmpty());
	}

	// Negative test cases

	@Test 
	void should_return_bad_request_when_get_all_loans_with_invalid_page_parameters() throws Exception {
		mockMvc.perform(get(BASE_URL).param("page", "-1").param("size", "0").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isBadRequest());
	}

	@Test 
	void should_return_404_when_get_all_loans_with_nonexistent_username() throws Exception {
		when(loanService.getAllLoans(any(), any(), any())).thenReturn(LoanFactory.emptyLoanResponsePage());

		mockMvc.perform(get(BASE_URL).param("username", "nonexistentUser").contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk()).andExpect(jsonPath("$.content").isArray())
				.andExpect(jsonPath("$.content").isEmpty());
	}

}
