package com.tfg.cultura.api.loans.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanStatusSchedulerTest {

	private static final ZoneId MADRID_ZONE = ZoneId.of("Europe/Madrid");

	@Mock
	private LoanRepository loanRepository;

	@InjectMocks
	private LoanStatusScheduler loanStatusScheduler;

	@Test
	void should_mark_overdue_loans_and_save_them() {
		Loan firstLoan = Loan.builder().status(LoanStatus.ACTIVE).build();
		Loan secondLoan = Loan.builder().status(LoanStatus.ACTIVE).build();
		Set<Loan> overdueLoans = Set.of(firstLoan, secondLoan);
		LocalDate today = LocalDate.now(MADRID_ZONE);
		when(loanRepository.findByStatusAndDueDateBefore(LoanStatus.ACTIVE, today)).thenReturn(overdueLoans);

		loanStatusScheduler.updateOverdueLoans();

		assertEquals(LoanStatus.OVERDUE, firstLoan.getStatus());
		assertEquals(LoanStatus.OVERDUE, secondLoan.getStatus());
		verify(loanRepository).findByStatusAndDueDateBefore(LoanStatus.ACTIVE, today);
		verify(loanRepository).saveAll(overdueLoans);
	}

	@Test
	void should_save_empty_set_when_there_are_no_overdue_loans() {
		LocalDate today = LocalDate.now(MADRID_ZONE);
		Set<Loan> overdueLoans = Set.of();
		when(loanRepository.findByStatusAndDueDateBefore(LoanStatus.ACTIVE, today)).thenReturn(overdueLoans);

		loanStatusScheduler.updateOverdueLoans();

		verify(loanRepository).findByStatusAndDueDateBefore(LoanStatus.ACTIVE, today);
		verify(loanRepository).saveAll(overdueLoans);
	}
}
