package com.tfg.cultura.api.loans.service;

import com.tfg.cultura.api.core.exception.NotFoundException;
import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.core.exception.UnauthorizedException;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.service.UserService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class LoanDeleteService extends LoanService {

	public LoanDeleteService(LoanRepository loanRepository, CustomUserDetailsService userDetailsService,
			UserService userService) {
		super(loanRepository, userDetailsService, userService);
	}

	@Transactional
	public void deleteLoan(String loanCode) throws NotFoundException, UnauthorizedException, UnathenticatedException {
		Loan loan = findLoanByCode(loanCode);

		// Si no es un administrador, solo puede eliminar sus propios préstamos
		checkCurrentUserIsLoanOwner(loan);

		loanRepository.delete(loan);
		logger.info("Préstamo {} eliminado por el usuario {}", loanCode,
				userDetailsService.getCurrentUserDetails().getId());
	}
}
