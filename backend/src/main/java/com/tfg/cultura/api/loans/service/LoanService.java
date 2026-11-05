package com.tfg.cultura.api.loans.service;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

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
import com.tfg.cultura.api.core.exception.UnauthorizedException;
import com.tfg.cultura.api.core.exception.ValidationException;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.dto.LoanCreateRequest;
import com.tfg.cultura.api.loans.model.dto.LoanResponse;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import com.tfg.cultura.api.loans.service.specifications.BoardGameLoanSpecification;
import com.tfg.cultura.api.loans.service.specifications.LoanSpecification;
import com.tfg.cultura.api.loans.service.specifications.RolGameLoanSpecification;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import com.tfg.cultura.api.users.model.enumerators.Role;
import com.tfg.cultura.api.users.service.UserService;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class LoanService {
	private final LoanRepository loanRepository;

	// SERVICES
	private final RolGameService rolGameService;
	private final BoardGameService boardGameService;
	private final BookService bookService;
	private final MovieService movieService;
	private final SeriesService seriesService;
	private final CustomUserDetailsService userDetailsService;
	private final LoanCodeGenerator loanCodeGenerator;
	private final UserService userService;

	// SPECIFICATIONS
	private final LoanSpecification<Movie> movieLoanSpecification;
	private final LoanSpecification<Book> bookLoanSpecification;
	private final LoanSpecification<Series> seriesLoanSpecification;
	private final BoardGameLoanSpecification boardGameLoanSpecification;
	private final RolGameLoanSpecification rolGameLoanSpecification;

	private static final Logger logger = LoggerFactory.getLogger("loansLogger");

	private BoardGame validateRequestedBoardGame(String itemId, String userId)
			throws NotFoundException, FieldException {
		BoardGame boardGame = boardGameService.findById(itemId);
		boardGameLoanSpecification.validate(boardGame, userId);
		boardGameService.setLoanUnavailable(boardGame);
		return boardGame;
	}

	private Book validateRequestedBook(String itemId, String userId) throws NotFoundException, FieldException {
		Book book = bookService.findById(itemId);
		bookLoanSpecification.validate(book, userId, ItemType.BOOK);
		bookService.setLoanUnavailable(book);
		return book;
	}

	private Movie validateRequestedMovie(String itemId, String userId) throws NotFoundException, FieldException {
		Movie movie = movieService.findById(itemId);
		movieLoanSpecification.validate(movie, userId, ItemType.MOVIE);
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
		seriesLoanSpecification.validate(series, userId, ItemType.SERIES);
		seriesService.setLoanUnavailable(series);
		return series;
	}

	// CREATE

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

	// READ

	private LoanResponse toResponse(Loan loan) throws NotFoundException {
		String username = userService.findUserById(loan.getUserId()).getUsername();
		return new LoanResponse(loan, username);
	}

	private Page<LoanResponse> toResponsePage(Page<Loan> loans) {
		return loans.map(loan -> {
			return toResponse(loan);
		});
	}

	public Page<LoanResponse> getAllLoans(Pageable pageable, String username, String itemId) throws NotFoundException {
		Page<Loan> loans = null;
		String userId = null;

		if (username != null) {
			userId = userService.findUserByUsername(username).getId();
		}

		if (userId != null && itemId != null) {
			loans = loanRepository.findByUserIdAndItemId(userId, itemId, pageable);
		} else if (userId != null) {
			loans = loanRepository.findByUserId(userId, pageable);
		} else if (itemId != null) {
			loans = loanRepository.findByItemId(itemId, pageable);
		} else {
			loans = loanRepository.findAll(pageable);
		}

		return toResponsePage(loans);
	}

	private Loan findLoanByCode(String loanCode) throws NotFoundException {
		return loanRepository.findByCode(loanCode).orElseThrow(() -> new NotFoundException(
				"No se ha encontrado el préstamo con código: " + sanitize(loanCode), logger));
	}

	private void checkCurrentUserIsLoanOwner(Loan loan)
			throws UnauthorizedException, UnathenticatedException, NotFoundException {
		CustomUserDetails currentUser = userDetailsService.getCurrentUserDetails();
		if (currentUser.getRole() == Role.SOCIO && !loan.getUserId().equals(currentUser.getId())) {
			throw new UnauthorizedException("No tiene permiso para acceder a este préstamo.");
		}
	}

	public LoanResponse getLoanByCode(String loanCode) throws NotFoundException {
		Loan loan = findLoanByCode(loanCode);

		// Si no es un administrador, solo puede acceder a sus propios préstamos
		checkCurrentUserIsLoanOwner(loan);

		return toResponse(loan);
	}

	// UPDATE

	private void validateNewStatus(LoanStatus oldStatus, LoanStatus newStatus, boolean isAdmin)
			throws ValidationException, UnauthorizedException {
		if (isAdmin) {
			validateNewStatusAdmin(oldStatus, newStatus);
		} else {
			validateNewStatusUser(oldStatus, newStatus);
		}
	}

	private void validateNewStatusUser(LoanStatus oldStatus, LoanStatus newStatus)
			throws ValidationException, UnauthorizedException {
		if (oldStatus != LoanStatus.REQUESTED)
			throw new UnauthorizedException("No tiene permiso para actualizar el estado del préstamo.");
		if (newStatus != LoanStatus.CANCELLED)
			throw new ValidationException(logger,
					Map.of("status", "Un préstamo solicitado solo puede pasar a CANCELADO."));
	}

	private void validateNewStatusAdmin(LoanStatus oldStatus, LoanStatus newStatus) throws ValidationException {
		switch (oldStatus) {
			case REQUESTED :
				if (newStatus != LoanStatus.ACTIVE && newStatus != LoanStatus.REJECTED)
					throw new ValidationException(logger,
							Map.of("status", "Un préstamo solicitado solo puede pasar a ACTIVO o RECHAZADO."));
			case ACTIVE :
				if (newStatus != LoanStatus.RETURNED)
					throw new ValidationException(logger,
							Map.of("status", "Un préstamo activo solo puede pasar a ATRASADO o DEVUELTO."));
			case OVERDUE :
				if (newStatus != LoanStatus.RETURNED)
					throw new ValidationException(logger,
							Map.of("status", "Un préstamo atrasado solo puede pasar a DEVUELTO."));
			default :
				throw new ValidationException(logger,
						Map.of("status", "Un préstamo rechazado, cancelado o devuelto no puede cambiar de estado."));
		}
	}

	private Integer getLoanDays(ItemType itemType, String itemId) throws NotFoundException {
		switch (itemType) {
			case BOOK :
				return bookService.findById(itemId).getLoanDays();
			case MOVIE :
				return movieService.findById(itemId).getLoanDays();
			case SERIES :
				return seriesService.findById(itemId).getLoanDays();
			case BOARDGAME :
				return boardGameService.findById(itemId).getLoanDays();
			case ROLGAME :
				return rolGameService.findById(itemId).getLoanDays();
			default :
				// RN-20: No hay préstamos de videojuegos
				throw new ValidationException(logger,
						Map.of("status", "No se puede actualizar el estado de este préstamo."));
		}
	}

	private Loan updateLoanDates(Loan loan, LoanStatus newStatus, ItemType itemType) throws NotFoundException {
		LocalDate currentDate = LocalDate.now(ZoneId.of("Europe/Madrid"));
		switch (newStatus) {
			case LoanStatus.CANCELLED :
				loan.setCancelDate(LocalDate.now());
			case LoanStatus.REJECTED :
				loan.setRejectionDate(LocalDate.now());
			case LoanStatus.ACTIVE :
				Integer loanDays = getLoanDays(itemType, loan.getItemId());
				loan.setStartDate(currentDate);
				loan.setDueDate(currentDate.plusDays(loanDays));
			case LoanStatus.RETURNED :
				loan.setReturnDate(currentDate);
			default :
				// No se actualizan fechas para los estados REQUESTED y OVERDUE
				break;
		}
		return loan;
	}

	@Transactional
	public LoanResponse updateLoanStatus(String loanCode, LoanStatus newStatus)
			throws NotFoundException, UnauthorizedException, UnathenticatedException {
		Loan loan = findLoanByCode(loanCode);

		// Si no es un administrador, solo puede actualizar sus propios préstamos
		checkCurrentUserIsLoanOwner(loan);
		validateNewStatus(loan.getStatus(), newStatus, userService.currentUserIsAdmin());
		updateLoanDates(loan, newStatus, loan.getItemType());

		loan.setStatus(newStatus);
		Loan updatedLoan = loanRepository.save(loan);
		logger.info("Préstamo {} actualizado a estado {} por el usuario {}", loanCode, newStatus,
				userDetailsService.getCurrentUserDetails().getId());
		return toResponse(updatedLoan);
	}

	// DELETE
	
	@Transactional 
	public void deleteLoan(String loanCode) throws NotFoundException, UnauthorizedException, UnathenticatedException {
		Loan loan = findLoanByCode(loanCode);

		// Si no es un administrador, solo puede eliminar sus propios préstamos
		checkCurrentUserIsLoanOwner(loan);

		loanRepository.delete(loan);
		logger.info("Préstamo {} eliminado por el usuario {}", loanCode, userDetailsService.getCurrentUserDetails().getId());
	}

}
