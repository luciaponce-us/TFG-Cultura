package com.tfg.cultura.api.loans.service;

import com.tfg.cultura.api.core.exception.NotFoundException;
import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.core.exception.UnauthorizedException;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.dto.LoanResponse;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.service.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class LoanReadService extends LoanService {

	public LoanReadService(LoanRepository loanRepository, CustomUserDetailsService userDetailsService,
			UserService userService) {
		super(loanRepository, userDetailsService, userService);
	}

	private Page<LoanResponse> toResponsePage(Page<Loan> loans) {
		return loans.map(loan -> {
			return toResponse(loan);
		});
	}

	public Page<LoanResponse> getAllLoans(Pageable pageable, String username, String itemId) throws NotFoundException {
		Page<Loan> loans = null;
		String userId = null;

		username = (username != null && !username.isBlank()) ? username : null;
		itemId = (itemId != null && !itemId.isBlank()) ? itemId : null;

		if (username != null) {
			userId = userService.findUserByUsername(username).getId();
		}

		if (userId != null && itemId != null) {
			loans = loanRepository.findByUserIdAndItemId(userId, itemId, pageable);
		} else if (userId != null) {
			loans = loanRepository.findByUserId(userId, pageable);
		} else if (itemId != null) {
			loans = loanRepository.findByItemId(itemId, pageable);
		} else {
			loans = loanRepository.findAll(pageable);
		}

		return toResponsePage(loans);
	}

	public LoanResponse getLoanByCode(String loanCode) throws NotFoundException, UnauthorizedException, UnathenticatedException {
		Loan loan = findLoanByCode(loanCode);

		// Si no es un administrador, solo puede acceder a sus propios préstamos
		checkCurrentUserIsLoanOwner(loan);

		return toResponse(loan);
	}

	public Page<LoanResponse> getMyLoans(Pageable pageable) throws NotFoundException, UnathenticatedException {
		CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();
		Page<Loan> loans = loanRepository.findByUserId(currentUser.getId(), pageable);
		return toResponsePage(loans);
	}

}
