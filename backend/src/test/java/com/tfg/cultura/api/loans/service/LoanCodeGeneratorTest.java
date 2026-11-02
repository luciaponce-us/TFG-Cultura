package com.tfg.cultura.api.loans.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.times;

import com.tfg.cultura.api.core.exception.FieldException;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class LoanCodeGeneratorTest {

    @Mock
    private LoanRepository loanRepository;

    @InjectMocks
    private LoanCodeGenerator loanCodeGenerator;

    private static final int MAX_CODE_GENERATION_ATTEMPTS = 10;
    private static final String EXPECTED_EXCEPTION_MESSAGE = "No se ha podido generar un código de préstamo único después de "
            + MAX_CODE_GENERATION_ATTEMPTS + " intentos";
    private static final String VALID_CODE_REGEX = "[A-Z]{3}[0-9]{3}";

    @Test
    void should_generate_available_code_with_expected_format() {
        when(loanRepository.existsByCode(anyString())).thenReturn(false);

        String code = loanCodeGenerator.generateUniqueCode();

        assertNotNull(code);
        assertTrue(code.matches(VALID_CODE_REGEX));
        verify(loanRepository).existsByCode(code);
    }

    @Test
    void should_retry_when_generated_code_already_exists() {
        when(loanRepository.existsByCode(anyString())).thenReturn(true, false);

        String code = loanCodeGenerator.generateUniqueCode();

        assertTrue(code.matches(VALID_CODE_REGEX));
        verify(loanRepository, times(2)).existsByCode(anyString());
    }

    @Test
    void should_throw_field_exception_after_maximum_generation_attempts() {
        when(loanRepository.existsByCode(anyString())).thenReturn(true);

        FieldException exception = assertThrows(FieldException.class,
                () -> loanCodeGenerator.generateUniqueCode());

        assertEquals(EXPECTED_EXCEPTION_MESSAGE,
                exception.getErrors().get("code"));
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, exception.getStatus());
        verify(loanRepository, times(10)).existsByCode(anyString());
    }

}
