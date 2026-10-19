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
import java.util.stream.Collectors;

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

		checkOnlyHasOneBaseGameAndHisExpansions(requestedGame, activeLoans);
	}

	private void checkOnlyHasOneBaseGameAndHisExpansions(BoardGame requestedGame, Set<Loan> activeLoans) throws FieldException {
		boolean isBaseGame = requestedGame.getBaseGame() == null;
		String baseGameId = isBaseGame ? requestedGame.getId() : requestedGame.getBaseGame().getId();
		Set<String> activeBaseGameIds = getActiveBaseGameIds(activeLoans);

		boolean hasMultipleBaseGames = activeBaseGameIds.stream().filter(id -> !id.equals(baseGameId)).count() > 0;

		if (hasMultipleBaseGames) {
			throw new FieldException(logger, HttpStatus.CONFLICT,
					Map.of("itemId", "Ya tienes varios juegos de mesa base o expansiones de otro juego base en tus préstamos activos"));
		}
	}

	private Set<String> getActiveBaseGameIds(Set<Loan> activeLoans) {
		return activeLoans.stream().map(loan -> {
			BoardGame loanedBoardGame = boardGameService.findById(loan.getItemId());
			boolean isBaseGame = loanedBoardGame.getBaseGame() == null;
			return isBaseGame ? loanedBoardGame.getId() : loanedBoardGame.getBaseGame().getId();
		}).collect(Collectors.toSet());
	}

}
