package com.tfg.cultura.api.loans.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.cultura.api.core.exception.NotFoundException;
import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.core.exception.UnauthorizedException;
import com.tfg.cultura.api.loans.factory.LoanFactory;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.service.UserService;

import static com.tfg.cultura.api.users.factory.UserFactory.mockLoggedUser;

@ExtendWith(MockitoExtension.class)
class LoanDeleteServiceTest {

    @Mock 
    private LoanRepository loanRepository;

    @Mock 
    private CustomUserDetailsService userDetailsService;

    @Mock 
    private UserService userService;

    @InjectMocks 
    private LoanDeleteService loanDeleteService;

    private Loan loan;

    @BeforeEach 
    void setUp() {
        loan = LoanFactory.boardGameLoan("currentUserId");
    }

    @Test 
    void should_delete_loan_successfully_when_current_user_is_owner() {
        when(loanRepository.findByCode(loan.getCode())).thenReturn(Optional.of(loan));
        mockLoggedUser(userDetailsService);
        when(userService.currentUserIsAdmin()).thenReturn(false);

        loanDeleteService.deleteLoan(loan.getCode());

        verify(loanRepository).delete(loan);
    }

    @Test 
    void should_delete_loan_successfully_when_current_user_is_admin() {
        when(loanRepository.findByCode(loan.getCode())).thenReturn(Optional.of(loan));
        mockLoggedUser(userDetailsService);
        when(userService.currentUserIsAdmin()).thenReturn(true);

        loanDeleteService.deleteLoan(loan.getCode());

        verify(loanRepository).delete(loan);
    }

    @Test 
    void should_delete_loan_successfully_when_current_user_is_admin_and_not_owner() {
        loan.setUserId("differentUserId");
        when(loanRepository.findByCode(loan.getCode())).thenReturn(Optional.of(loan));
        mockLoggedUser(userDetailsService);
        when(userService.currentUserIsAdmin()).thenReturn(true);

        loanDeleteService.deleteLoan(loan.getCode());

        verify(loanRepository).delete(loan);
    }

    @Test 
    void should_throw_exception_when_loan_not_found() {
        when(loanRepository.findByCode(loan.getCode())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> loanDeleteService.deleteLoan(loan.getCode()));
    }

    @Test 
    void should_throw_exception_when_user_not_authorized() {
        loan.setUserId("differentUserId");
        when(loanRepository.findByCode(loan.getCode())).thenReturn(Optional.of(loan));
        mockLoggedUser(userDetailsService);
        when(userService.currentUserIsAdmin()).thenReturn(false);

        assertThrows(UnauthorizedException.class, () -> loanDeleteService.deleteLoan(loan.getCode()));
    }

    @Test 
    void should_throw_exception_when_user_not_authenticated() {
        loan.setUserId("differentUserId");
        when(loanRepository.findByCode(loan.getCode())).thenReturn(Optional.of(loan));
        when(userDetailsService.getCurrentUserDetails()).thenThrow(new UnathenticatedException("User not authenticated"));

        assertThrows(UnathenticatedException.class, () -> loanDeleteService.deleteLoan(loan.getCode()));
    }

}
