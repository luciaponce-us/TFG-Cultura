package com.tfg.cultura.api.loans.model;

import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.loans.validation.annotations.ValidLoanCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(collection = "loans")
public class Loan {
    @Id 
    private String id;
    
    @ValidLoanCode
    @Indexed(unique = true)
    @NotBlank(message = "El código del préstamo es obligatorio")
    private String code;

    @NotNull(message = "El estado del préstamo es obligatorio")
    @Builder.Default
    private LoanStatus status = LoanStatus.REQUESTED;

    @NotNull(message = "La fecha de solicitud es obligatoria")
    @CreatedDate
    private LocalDate requestDate; // REQUESTED

    private LocalDate cancelDate; // CANCELLED

    private LocalDate startDate; // ACTIVE

    private LocalDate dueDate; // ACTIVE

    private LocalDate returnDate; // RETURNED

    private LocalDate rejectionDate; // REJECTED

    @NotNull(message = "El usuario es obligatorio")
    @NotBlank(message = "El usuario es obligatorio")
    private String userId;

    @NotNull(message = "El ítem es obligatorio")
    @NotBlank(message = "El ítem es obligatorio")
    private String itemId;

    @NotNull(message = "El tipo de ítem es obligatorio")
    private ItemType itemType;
}
