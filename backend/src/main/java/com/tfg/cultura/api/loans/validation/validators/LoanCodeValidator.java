package com.tfg.cultura.api.loans.validation.validators;

import com.tfg.cultura.api.loans.validation.annotations.ValidLoanCode;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class LoanCodeValidator implements ConstraintValidator<ValidLoanCode, String> {

	@Override
	public boolean isValid(String code, ConstraintValidatorContext context) {
		if (code == null || code.isEmpty())
			return true; // campo opcional

		return code.matches("^[A-Z]{3}\\d{3}$"); // Validación del formato del código de préstamo (ejemplo: ABC123)
	}

}
