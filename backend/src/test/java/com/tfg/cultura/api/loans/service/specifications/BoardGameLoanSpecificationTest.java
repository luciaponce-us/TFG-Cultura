package com.tfg.cultura.api.loans.service.specifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.BoardGame;
import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.catalog.service.BoardGameService;
import com.tfg.cultura.api.core.exception.FieldException;
import com.tfg.cultura.api.loans.factory.LoanFactory;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.loans.repository.LoanRepository;

@ExtendWith(MockitoExtension.class)
class BoardGameLoanSpecificationTest {
    @Mock 
    private LoanRepository loanRepository;

    @Mock 
    private BoardGameService boardGameService;

    @InjectMocks 
    private BoardGameLoanSpecification specification;

    private BoardGame requestedGame;
    private Loan activeLoan;
    private BoardGame activeLoanedGame;

    @BeforeEach 
    void setUp() {
        requestedGame = CatalogFactory.validBoardGame();
        activeLoan = LoanFactory.boardGameLoan("userId");
        activeLoanedGame = CatalogFactory.validBoardGame2();
    }

    void mockActiveLoan(BoardGame loanedGame) {
        Set<Loan> activeLoans = Set.of();

        if(loanedGame != null) {
            when(boardGameService.findById(activeLoan.getItemId())).thenReturn(loanedGame);
            activeLoans = Set.of(activeLoan);
        }

        when(loanRepository.findByUserIdAndStatusAndItemType(anyString(), eq(LoanStatus.ACTIVE),
                eq(ItemType.BOARDGAME))).thenReturn(activeLoans);
    }

    @Test 
    void should_validate_successfully_when_no_active_loans() {
        requestedGame.setLoanAvailable(true);
        mockActiveLoan(null);

        specification.validate(requestedGame, "userId");

        verify(loanRepository).findByUserIdAndStatusAndItemType("userId", LoanStatus.ACTIVE, ItemType.BOARDGAME);
    }

    @Test 
    void should_trow_field_exception_when_requested_game_is_not_available() {
        requestedGame.setLoanAvailable(false);

        FieldException ex = assertThrows(FieldException.class, () -> {
            specification.validate(requestedGame, "userId");
        });

        assertTrue(ex.getErrors().containsKey("itemId"));
        assertTrue(ex.getErrors().get("itemId").contains("disponible"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());

        verifyNoInteractions(loanRepository);
    }

    // checkOnlyHasOneBaseGameAndHisExpansions

    @Test 
    void should_throw_conflict_when_concurrent_base_games(){
        requestedGame.setLoanAvailable(true);
        requestedGame.setBaseGame(null); // is base game
        activeLoanedGame.setBaseGame(null); // is base game
        mockActiveLoan(activeLoanedGame);

        FieldException ex = assertThrows(FieldException.class, () -> {
            specification.validate(requestedGame, "userId");
        });

        assertTrue(ex.getErrors().containsKey("itemId"));
        assertTrue(ex.getErrors().get("itemId").contains("préstamos activos"));
        assertEquals(HttpStatus.CONFLICT, ex.getStatus());

        verify(loanRepository).findByUserIdAndStatusAndItemType("userId", LoanStatus.ACTIVE, ItemType.BOARDGAME);
        verify(loanRepository).findByUserIdAndStatusAndItemType("userId", LoanStatus.ACTIVE, ItemType.BOARDGAME);
    }
    @Test
    void should_validate_successfully_when_requested_expansion_has_same_base_game_as_active_loan() {
        requestedGame.setLoanAvailable(true);
        activeLoanedGame.setId("basegame");
        activeLoanedGame.setBaseGame(null);
        mockActiveLoan(activeLoanedGame);

        specification.validate(requestedGame, "userId");
    }

    @Test
    void should_validate_successfully_when_requested_base_game_matches_active_expansion() {
        requestedGame.setLoanAvailable(true);
        requestedGame.setBaseGame(null);
        requestedGame.setId("basegame");
        activeLoanedGame.setBaseGame(CatalogFactory.validBaseBoardGame());
        mockActiveLoan(activeLoanedGame);

        specification.validate(requestedGame, "userId");
    }
    
}
