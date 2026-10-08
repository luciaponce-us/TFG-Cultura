package com.tfg.cultura.api.loans.model.dto;

import java.time.LocalDate;

import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import lombok.Getter;

@Getter 
public class LoanResponse {
    private String id;
    private String code;
    private LoanStatus status;
    private LocalDate requestDate; // REQUESTED
    private LocalDate cancelDate; // CANCELLED
    private LocalDate startDate; // ACTIVE
    private LocalDate dueDate; // ACTIVE
    private LocalDate returnDate; // RETURNED
    private LocalDate rejectionDate; // REJECTED
    private String username;
    private String itemId;

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
