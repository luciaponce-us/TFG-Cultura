package com.tfg.cultura.api.loans.controller;

import com.tfg.cultura.api.loans.model.dto.LoanCreateRequest;
import com.tfg.cultura.api.loans.model.dto.LoanResponse;
import com.tfg.cultura.api.loans.service.LoanService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/loans")
@RequiredArgsConstructor
@Tag(name = "Loans", description = "Gestión de préstamos")
public class LoanController {
	private final LoanService loanService;

	@PostMapping
	public ResponseEntity<LoanResponse> createLoan(@Valid @RequestBody LoanCreateRequest request) {
		LoanResponse loanResponse = loanService.createLoan(request);
		return ResponseEntity.status(HttpStatus.CREATED).body(loanResponse);
	}

	@GetMapping
	public ResponseEntity<Page<LoanResponse>> getAllLoans(@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size, @RequestParam(required = false) String username,
			@RequestParam(required = false) String itemId) {
		Page<LoanResponse> loans = loanService.getAllLoans(PageRequest.of(page, size), username, itemId);
		return ResponseEntity.status(HttpStatus.OK).body(loans);
	}

	@GetMapping("/{loanCode}")
	public ResponseEntity<LoanResponse> getLoanByCode(@PathVariable String loanCode) {
		LoanResponse loanResponse = loanService.getLoanByCode(loanCode);
		return ResponseEntity.status(HttpStatus.OK).body(loanResponse);
	}

}
