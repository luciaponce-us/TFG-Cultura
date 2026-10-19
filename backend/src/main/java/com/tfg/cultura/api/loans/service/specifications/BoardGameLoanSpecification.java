package com.tfg.cultura.api.loans.service.specifications;

import com.tfg.cultura.api.catalog.model.BoardGame;
import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.catalog.service.BoardGameService;
import com.tfg.cultura.api.core.exception.FieldException;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class BoardGameLoanSpecification implements LoanSpecification<BoardGame> {

	private static final Logger logger = LoggerFactory.getLogger("loansLogger");

	private final LoanRepository loanRepository;
	private final BoardGameService boardGameService;

	@Override
	public void validate(BoardGame requestedGame, String userId) throws FieldException {
		if (!requestedGame.getLoanAvailable()) {
			throw new FieldException(logger, HttpStatus.CONFLICT,
					Map.of("itemId", "El juego de mesa no está disponible para préstamo"));
		}

		Set<Loan> activeLoans = loanRepository.findByUserIdAndStatusAndItemType(userId, LoanStatus.ACTIVE,
				ItemType.BOARDGAME);

		if (activeLoans.isEmpty()) {
			return;
		}

		String baseGameId = getBaseGameId(requestedGame);
		checkNotConcurrentBaseGames(baseGameId == null);
		// Expansiones con otro juego base
		checkNotExpansionsWithOtherBaseGame(baseGameId, activeLoans);
		// Otro juego base que no sea el del juego solicitado
		checkNotOtherBaseGamesUnrelatedWithRequestedExpansion(baseGameId, activeLoans);
	}

	private String getBaseGameId(BoardGame requestedBoardGame) {
		return requestedBoardGame.getBaseGame() != null ? requestedBoardGame.getBaseGame().getId() : null;
	}

	private void checkNotConcurrentBaseGames(boolean isBaseGame) {
		if (isBaseGame) {
			throw new FieldException(logger, HttpStatus.CONFLICT,
					Map.of("itemId", "No se puede solicitar un préstamo de varios juegos de mesa base concurrentes."));
		}
	}

	private void checkNotExpansionsWithOtherBaseGame(String baseGameId, Set<Loan> loansInSameSection) {
		boolean differentBaseGame = loansInSameSection.stream().anyMatch(loan -> {
			BoardGame loanedBoardGame = boardGameService.findById(loan.getItemId());
			String loanedBaseGameId = loanedBoardGame.getBaseGame() != null
					? loanedBoardGame.getBaseGame().getId()
					: null;
			return !baseGameId.equals(loanedBaseGameId) && loanedBaseGameId != null;
		});

		if (differentBaseGame) {
			throw new FieldException(logger, HttpStatus.CONFLICT, Map.of("itemId",
					"No se puede solicitar un préstamo de varios juegos de mesa de diferentes bases concurrentes."));
		}
	}

	private void checkNotOtherBaseGamesUnrelatedWithRequestedExpansion(String baseGameId,
			Set<Loan> loansInSameSection) {
		boolean otherBaseGameLoaned = loansInSameSection.stream().anyMatch(loan -> {
			BoardGame loanedBoardGame = boardGameService.findById(loan.getItemId());
			boolean isBaseGameOfRequested = loanedBoardGame.getId().equals(baseGameId);
			return loanedBoardGame.getBaseGame() == null && !isBaseGameOfRequested;
		});
		if (otherBaseGameLoaned) {
			throw new FieldException(logger, HttpStatus.CONFLICT,
					Map.of("itemId", "No se puede solicitar un préstamo de varios juegos de mesa base concurrentes."));
		}
	}

}
