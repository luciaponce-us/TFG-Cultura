package com.tfg.cultura.api.loans.service;

import com.mongodb.DuplicateKeyException;
import com.tfg.cultura.api.catalog.model.BoardGame;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Movie;
import com.tfg.cultura.api.catalog.model.RolGame;
import com.tfg.cultura.api.catalog.model.Series;
import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.catalog.service.BoardGameService;
import com.tfg.cultura.api.catalog.service.BookService;
import com.tfg.cultura.api.catalog.service.MovieService;
import com.tfg.cultura.api.catalog.service.RolGameService;
import com.tfg.cultura.api.catalog.service.SeriesService;
import com.tfg.cultura.api.core.exception.FieldException;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.dto.LoanCreateRequest;
import com.tfg.cultura.api.loans.model.dto.LoanResponse;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanService {
	private final LoanRepository loanRepository;
	private final RolGameService rolGameService;
	private final BoardGameService boardGameService;
	private final BookService bookService;
	private final MovieService movieService;
	private final SeriesService seriesService;
	private final CustomUserDetailsService userDetailsService;

	private final static Logger logger = LoggerFactory.getLogger("loansLogger");

	private BoardGame validateRequestedBoardGame(String itemId, String userId) {
		BoardGame boardGame = boardGameService.findById(itemId);
		if (!boardGame.getLoanAvailable()) {
			throw new FieldException(logger, HttpStatus.CONFLICT,
					Map.of("itemId", "El juego de mesa no está disponible para préstamo"));
		}

		Set<Loan> activeBoardGameLoans = findAllActiveLoansByUserIdAndItemType(userId, ItemType.BOARDGAME);
		if (!activeBoardGameLoans.isEmpty()) {
			String baseGameId = getBaseGameId(boardGame);
			checkNotConcurrentBaseGames(baseGameId == null);
			// Expansiones con otro juego base
			checkNotExpansionsWithOtherBaseGame(baseGameId, activeBoardGameLoans);
			// Otro juego base que no sea el del juego solicitado
			checkNotOtherBaseGamesUnrelatedWithRequestedExpansion(baseGameId, activeBoardGameLoans);
		}
		boardGameService.setLoanUnavailable(boardGame);
		return boardGame;
	}

	private Book validateRequestedBook(String itemId, String userId) {
		Book book = bookService.findById(itemId);
		if (!book.getLoanAvailable()) {
			throw new FieldException(logger, HttpStatus.CONFLICT,
					Map.of("itemId", "El libro no está disponible para préstamo"));
		}

		Set<Loan> activeBookLoans = findAllActiveLoansByUserIdAndItemType(userId, ItemType.BOOK);
		if (!activeBookLoans.isEmpty()) {
			throw new FieldException(logger, HttpStatus.CONFLICT,
					Map.of("itemId", "No se puede solicitar un préstamo de varios libros concurrentemente."));
		}
		bookService.setLoanUnavailable(book);
		return book;
	}

	private Movie validateRequestedMovie(String itemId, String userId) {
		Movie movie = movieService.findById(itemId);
		if (!movie.getLoanAvailable()) {
			throw new FieldException(logger, HttpStatus.CONFLICT,
					Map.of("itemId", "La película no está disponible para préstamo"));
		}

		Set<Loan> activeMovieLoans = findAllActiveLoansByUserIdAndItemType(userId, ItemType.MOVIE);
		if (!activeMovieLoans.isEmpty()) {
			throw new FieldException(logger, HttpStatus.CONFLICT,
					Map.of("itemId", "No se puede solicitar un préstamo de varias películas concurrentemente."));
		}

		movieService.setLoanUnavailable(movie);

		return movie;
	}

	private RolGame validateRequestedRolGame(String itemId, String userId) {
		RolGame rolGame = rolGameService.findById(itemId);
		if (!rolGame.getLoanAvailable()) {
			throw new FieldException(logger, HttpStatus.CONFLICT,
					Map.of("itemId", "El juego de rol no está disponible para préstamo"));
		}

		Set<Loan> activeRolGameLoans = findAllActiveLoansByUserIdAndItemType(userId, ItemType.ROLGAME);
		if (!activeRolGameLoans.isEmpty()) {
			checkAllRolGamesAreSameSaga(activeRolGameLoans, rolGame);
		}

		rolGameService.setLoanUnavailable(rolGame);
		return rolGame;
	}

	private Series validateRequestedSeries(String itemId, String userId) {
		Series series = seriesService.findById(itemId);
		if (!series.getLoanAvailable()) {
			throw new FieldException(logger, HttpStatus.CONFLICT,
					Map.of("itemId", "La serie no está disponible para préstamo"));
		}
		Set<Loan> activeSeriesLoans = findAllActiveLoansByUserIdAndItemType(userId, ItemType.SERIES);
		if (!activeSeriesLoans.isEmpty()) {
			throw new FieldException(logger, HttpStatus.CONFLICT,
					Map.of("itemId", "No se puede solicitar un préstamo de varias series concurrentemente."));
		}
		seriesService.setLoanUnavailable(series);
		return series;
	}

	private void checkAllRolGamesAreSameSaga(Set<Loan> loansInSameSection, RolGame requestedRolGame) {
		String requestedRolGameSagaId = requestedRolGame.getSaga().getId();
		boolean differentSaga = loansInSameSection.stream().anyMatch(
				loan -> !rolGameService.findById(loan.getItemId()).getSaga().getId().equals(requestedRolGameSagaId));

		if (differentSaga) {
			throw new FieldException(logger, HttpStatus.CONFLICT, Map.of("itemId",
					"No se puede solicitar un préstamo de un juego de rol de una saga diferente mientras se tenga un préstamo activo de otro juego de rol"));
		}
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

	@Transactional
	public LoanResponse createLoan(LoanCreateRequest request) {
		CustomUserDetails loggedUser = userDetailsService.getCurrentUserDetails();
		String userId = loggedUser.getId();

		switch (request.getItemType()) {
			case BOARDGAME :
				BoardGame boardGame = validateRequestedBoardGame(request.getItemId(), userId);
				logger.info("Usuario {} solicita préstamo del juego de mesa {} ({})", userId, boardGame.getName(),
						boardGame.getId());
				break;
			case BOOK :
				Book book = validateRequestedBook(request.getItemId(), userId);
				logger.info("Usuario {} solicita préstamo del libro {} ({})", userId, book.getName(), book.getId());
				break;
			case MOVIE :
				Movie movie = validateRequestedMovie(request.getItemId(), userId);
				logger.info("Usuario {} solicita préstamo de la película {} ({})", userId, movie.getName(),
						movie.getId());
				break;
			case ROLGAME :
				RolGame rolGame = validateRequestedRolGame(request.getItemId(), userId);
				logger.info("Usuario {} solicita préstamo del juego de rol {} ({})", userId, rolGame.getName(),
						rolGame.getId());
				break;
			case SERIES :
				Series series = validateRequestedSeries(request.getItemId(), userId);
				logger.info("Usuario {} solicita préstamo de la serie {} ({})", userId, series.getName(),
						series.getId());
				break;
			case VIDEOGAME :
				// RN-20: No hay préstamos de videojuegos
				throw new FieldException(logger, HttpStatus.BAD_REQUEST,
						Map.of("itemType", "No se puede solicitar un préstamo de un videojuego."));
		}

		Loan loan = Loan.builder().userId(userId).itemId(request.getItemId()).itemType(request.getItemType())
				.code(generateUniqueCode()).build();

		try {
			Loan savedLoan = loanRepository.save(loan);
			return new LoanResponse(savedLoan, loggedUser.getUsername());
		} catch (DuplicateKeyException e) {
			// Excepción que podría ocurrir por concurrencia
			throw new FieldException(logger, HttpStatus.INTERNAL_SERVER_ERROR,
					Map.of("code", "No se ha podido generar un código de préstamo único después de "
							+ MAX_CODE_GENERATION_ATTEMPTS + " intentos"));
		}
	}

	private Set<Loan> findAllActiveLoansByUserIdAndItemType(String userId, ItemType itemType) {
		return loanRepository.findByUserIdAndStatusAndItemType(userId, LoanStatus.ACTIVE, itemType);
	}

	private static final String LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
	private static final String DIGITS = "0123456789";

	private String generateCode() {
		StringBuilder code = new StringBuilder(6);

		for (int i = 0; i < 3; i++) {
			code.append(LETTERS.charAt(ThreadLocalRandom.current().nextInt(LETTERS.length())));
		}

		for (int i = 0; i < 3; i++) {
			code.append(DIGITS.charAt(ThreadLocalRandom.current().nextInt(DIGITS.length())));
		}

		return code.toString();
	}

	private static final int MAX_CODE_GENERATION_ATTEMPTS = 10;

	private String generateUniqueCode() {
		for (int attempt = 0; attempt < MAX_CODE_GENERATION_ATTEMPTS; attempt++) {
			String code = generateCode();

			if (!loanRepository.existsByCode(code)) {
				return code;
			}
		}

		throw new FieldException(logger, HttpStatus.INTERNAL_SERVER_ERROR,
				Map.of("code", "No se ha podido generar un código de préstamo único después de "
						+ MAX_CODE_GENERATION_ATTEMPTS + " intentos"));
	}

}
