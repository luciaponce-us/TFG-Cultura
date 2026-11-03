package com.tfg.cultura.api.loans.service;

import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LoanStatusScheduler {
	private final LoanRepository loanRepository;

	@Scheduled(cron = "0 0 0 * * *", zone = "Europe/Madrid")
	public void updateOverdueLoans() {
		LocalDate today = LocalDate.now(ZoneId.of("Europe/Madrid"));

		Set<Loan> overdueLoans = loanRepository.findByStatusAndDueDateBefore(LoanStatus.ACTIVE, today);

		overdueLoans.forEach(loan -> loan.setStatus(LoanStatus.OVERDUE));

		loanRepository.saveAll(overdueLoans);
	}
}
