package com.tfg.cultura.api.loans.service;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

import com.tfg.cultura.api.core.exception.NotFoundException;
import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.core.exception.UnauthorizedException;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.dto.LoanResponse;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.service.UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LoanService {

	protected final LoanRepository loanRepository;

	protected final CustomUserDetailsService userDetailsService;
	protected final UserService userService;

	protected static final Logger logger = LoggerFactory.getLogger("loansLogger");

	protected LoanResponse toResponse(Loan loan) throws NotFoundException {
		String username = userService.findUserById(loan.getUserId()).getUsername();
		return new LoanResponse(loan, username);
	}

	protected Loan findLoanByCode(String loanCode) throws NotFoundException {
		return loanRepository.findByCode(loanCode).orElseThrow(() -> new NotFoundException(
				"No se ha encontrado el préstamo con código: " + sanitize(loanCode), logger));
	}

	protected void checkCurrentUserIsLoanOwner(Loan loan)
			throws UnauthorizedException, UnathenticatedException, NotFoundException {
		String currentUserId = userDetailsService.getCurrentUserDetails().getId();
		boolean isAdmin = userService.currentUserIsAdmin();
		boolean isLoanOwner = loan.getUserId().equals(currentUserId);
		if (!isAdmin && !isLoanOwner) {
			throw new UnauthorizedException("No tiene permiso para acceder a este préstamo.");
		}
	}

}
