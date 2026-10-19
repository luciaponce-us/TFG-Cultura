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
import com.tfg.cultura.api.core.exception.NotFoundException;
import com.tfg.cultura.api.core.exception.UnathenticatedException;
import com.tfg.cultura.api.core.exception.ValidationException;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.dto.LoanCreateRequest;
import com.tfg.cultura.api.loans.model.dto.LoanResponse;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import com.tfg.cultura.api.loans.service.specifications.BoardGameLoanSpecification;
import com.tfg.cultura.api.loans.service.specifications.RolGameLoanSpecification;
import com.tfg.cultura.api.loans.service.specifications.SingleActiveLoanSpecification;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import java.util.Map;
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
	private final LoanCodeGenerator loanCodeGenerator;

	private final SingleActiveLoanSpecification singleActiveLoanSpecification;
	private final BoardGameLoanSpecification boardGameLoanSpecification;
	private final RolGameLoanSpecification rolGameLoanSpecification;

	private final static Logger logger = LoggerFactory.getLogger("loansLogger");

	private BoardGame validateRequestedBoardGame(String itemId, String userId)
			throws NotFoundException, FieldException {
		BoardGame boardGame = boardGameService.findById(itemId);
		boardGameLoanSpecification.validate(boardGame, userId);
		boardGameService.setLoanUnavailable(boardGame);
		return boardGame;
	}

	private Book validateRequestedBook(String itemId, String userId) throws NotFoundException, FieldException {
		Book book = bookService.findById(itemId);
		singleActiveLoanSpecification.validate(userId, ItemType.BOOK);
		bookService.setLoanUnavailable(book);
		return book;
	}

	private Movie validateRequestedMovie(String itemId, String userId) throws NotFoundException, FieldException {
		Movie movie = movieService.findById(itemId);
		singleActiveLoanSpecification.validate(userId, ItemType.MOVIE);
		movieService.setLoanUnavailable(movie);
		return movie;
	}

	private RolGame validateRequestedRolGame(String itemId, String userId) throws NotFoundException, FieldException {
		RolGame rolGame = rolGameService.findById(itemId);
		rolGameLoanSpecification.validate(rolGame, userId);
		rolGameService.setLoanUnavailable(rolGame);
		return rolGame;
	}

	private Series validateRequestedSeries(String itemId, String userId) throws NotFoundException, FieldException {
		Series series = seriesService.findById(itemId);
		singleActiveLoanSpecification.validate(userId, ItemType.SERIES);
		seriesService.setLoanUnavailable(series);
		return series;
	}

	@Transactional
	public LoanResponse createLoan(LoanCreateRequest request)
			throws UnathenticatedException, NotFoundException, FieldException, ValidationException {
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
				throw new ValidationException(logger,
						Map.of("itemType", "No se puede solicitar un préstamo de un videojuego."));
		}

		Loan loan = Loan.builder().userId(userId).itemId(request.getItemId()).itemType(request.getItemType())
				.code(loanCodeGenerator.generateUniqueCode()).build();

		try {
			Loan savedLoan = loanRepository.save(loan);
			return new LoanResponse(savedLoan, loggedUser.getUsername());
		} catch (DuplicateKeyException e) {
			// Excepción que podría ocurrir por concurrencia
			throw new FieldException(logger, HttpStatus.INTERNAL_SERVER_ERROR,
					Map.of("code", "No se ha podido generar un código de préstamo único. Inténtelo de nuevo."));
		}
	}

}
