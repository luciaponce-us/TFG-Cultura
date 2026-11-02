package com.tfg.cultura.api.loans.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;

import com.tfg.cultura.api.loans.factory.LoanFactory;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.dto.LoanCreateRequest;
import com.tfg.cultura.api.loans.model.dto.LoanResponse;
import com.tfg.cultura.api.loans.service.LoanService;
import com.tfg.cultura.api.utils.BaseControllerTest;

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

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(loanCreateRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(loanResponse.getId()))
                .andExpect(jsonPath("$.code").value(loanResponse.getCode()))
                .andExpect(jsonPath("$.status").value(loanResponse.getStatus().toString()))
                .andExpect(jsonPath("$.username").value(loanResponse.getUsername()))
                .andExpect(jsonPath("$.itemId").value(loanResponse.getItemId()));
    }

    // Negative test cases

    @Test
    void should_return_bad_request_when_create_loan_with_invalid_request() throws Exception {
        LoanCreateRequest invalidRequest = new LoanCreateRequest(); // Empty request

        mockMvc.perform(post(BASE_URL)
                .contentType(MediaType.APPLICATION_JSON)
                .content(toJson(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

}
