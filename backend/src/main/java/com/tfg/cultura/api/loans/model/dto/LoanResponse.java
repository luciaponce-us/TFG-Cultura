package com.tfg.cultura.api.loans.model.dto;

import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import java.time.LocalDate;
import lombok.Getter;

@Getter
public class LoanResponse {
	private final String id;
	private final String code;
	private final LoanStatus status;
	private final LocalDate requestDate; // REQUESTED
	private final LocalDate cancelDate; // CANCELLED
	private final LocalDate startDate; // ACTIVE
	private final LocalDate dueDate; // ACTIVE
	private final LocalDate returnDate; // RETURNED
	private final LocalDate rejectionDate; // REJECTED
	private final String username;
	private final String itemId;

	public LoanResponse(Loan loan, String username) {
		this.id = loan.getId();
		this.code = loan.getCode();
		this.status = loan.getStatus();
		this.requestDate = loan.getRequestDate();
		this.cancelDate = loan.getCancelDate();
		this.startDate = loan.getStartDate();
		this.dueDate = loan.getDueDate();
		this.returnDate = loan.getReturnDate();
		this.rejectionDate = loan.getRejectionDate();
		this.username = username;
		this.itemId = loan.getItemId();
	}
}
