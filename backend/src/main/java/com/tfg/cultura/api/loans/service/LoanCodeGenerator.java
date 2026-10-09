package com.tfg.cultura.api.loans.service;

import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.tfg.cultura.api.core.exception.FieldException;
import com.tfg.cultura.api.loans.repository.LoanRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class LoanCodeGenerator {
    private static final Logger logger = LoggerFactory.getLogger("loansLogger");

    private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String DIGITS = "0123456789";

    private static final int LETTER_COUNT = 3;
    private static final int DIGIT_COUNT = 3;
    private static final int MAX_CODE_GENERATION_ATTEMPTS = 10;

    private final LoanRepository loanRepository;

    public String generateUniqueCode() {
        for (int attempt = 0; attempt < MAX_CODE_GENERATION_ATTEMPTS; attempt++) {

            String code = generateCode();

            if (!loanRepository.existsByCode(code)) {
                return code;
            }
        }

        throw new FieldException(
                logger,
                HttpStatus.INTERNAL_SERVER_ERROR,
                Map.of(
                        "code",
                        "No se ha podido generar un código de préstamo único después de "
                                + MAX_CODE_GENERATION_ATTEMPTS + " intentos"));
    }

    private String generateCode() {
        ThreadLocalRandom random = ThreadLocalRandom.current();

        StringBuilder code = new StringBuilder(
                LETTER_COUNT + DIGIT_COUNT);

        for (int i = 0; i < LETTER_COUNT; i++) {
            code.append(LETTERS.charAt(random.nextInt(LETTERS.length())));
        }

        for (int i = 0; i < DIGIT_COUNT; i++) {
            code.append(DIGITS.charAt(random.nextInt(DIGITS.length())));
        }

        return code.toString();
    }
}
