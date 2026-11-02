package com.tfg.cultura.api.loans.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;

import com.mongodb.DuplicateKeyException;
import com.tfg.cultura.api.catalog.factory.CatalogFactory;
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
import com.tfg.cultura.api.core.exception.ValidationException;
import com.tfg.cultura.api.core.factory.ExceptionsFactory;
import com.tfg.cultura.api.loans.factory.LoanFactory;
import com.tfg.cultura.api.loans.model.dto.LoanCreateRequest;
import com.tfg.cultura.api.loans.model.dto.LoanResponse;
import com.tfg.cultura.api.loans.repository.LoanRepository;
import com.tfg.cultura.api.loans.service.specifications.BoardGameLoanSpecification;
import com.tfg.cultura.api.loans.service.specifications.LoanSpecification;
import com.tfg.cultura.api.loans.service.specifications.RolGameLoanSpecification;
import com.tfg.cultura.api.users.factory.UserFactory;
import com.tfg.cultura.api.users.jwt.CustomUserDetails;
import com.tfg.cultura.api.users.jwt.CustomUserDetailsService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class LoanServiceTest {
	@Mock
	private LoanRepository loanRepository;

	@Mock
	private RolGameService rolGameService;

	@Mock
	private BoardGameService boardGameService;

	@Mock
	private BookService bookService;

	@Mock
	private MovieService movieService;

	@Mock
	private SeriesService seriesService;

	@Mock
	private CustomUserDetailsService userDetailsService;

	@Mock
	private LoanCodeGenerator loanCodeGenerator;

	@Mock
	private LoanSpecification<Movie> movieLoanSpecification;

	@Mock
	private LoanSpecification<Book> bookLoanSpecification;

	@Mock
	private LoanSpecification<Series> seriesLoanSpecification;

	@Mock
	private BoardGameLoanSpecification boardGameLoanSpecification;

	@Mock
	private RolGameLoanSpecification rolGameLoanSpecification;

	private LoanService loanService;

	private BoardGame boardGame;
	private LoanCreateRequest createRequestBoardGame;
	private Book book;
	private LoanCreateRequest createRequestBook;
	private Movie movie;
	private LoanCreateRequest createRequestMovie;
	private RolGame rolGame;
	private LoanCreateRequest createRequestRolGame;
	private Series series;
	private LoanCreateRequest createRequestSeries;
	private LoanCreateRequest createRequestVideoGame;

	@BeforeEach
	void setUp() {
		// No se puede usar @InjectMocks porque no distingue entre los LoanSpecification
		// genéricos y los específicos
		loanService = new LoanService(loanRepository, rolGameService, boardGameService, bookService, movieService,
				seriesService, userDetailsService, loanCodeGenerator, movieLoanSpecification, bookLoanSpecification,
				seriesLoanSpecification, boardGameLoanSpecification, rolGameLoanSpecification);

		createRequestBoardGame = LoanFactory.validLoanCreateRequest(ItemType.BOARDGAME);
		boardGame = CatalogFactory.validBoardGame();
		createRequestBook = LoanFactory.validLoanCreateRequest(ItemType.BOOK);
		book = CatalogFactory.validBook();
		createRequestMovie = LoanFactory.validLoanCreateRequest(ItemType.MOVIE);
		movie = CatalogFactory.validMovie();
		createRequestRolGame = LoanFactory.validLoanCreateRequest(ItemType.ROLGAME);
		rolGame = CatalogFactory.validRolGame();
		createRequestSeries = LoanFactory.validLoanCreateRequest(ItemType.SERIES);
		series = CatalogFactory.validSeries();
		createRequestVideoGame = LoanFactory.validLoanCreateRequest(ItemType.VIDEOGAME);
	}

	// HELPERS

	private void mockLoggedUser() {
		CustomUserDetails userDetails = UserFactory.mockAuthContext(); // id = currentUserId
		when(userDetailsService.getCurrentUserDetails()).thenReturn(userDetails);
	}

	private void mockLoanCode() {
		when(loanCodeGenerator.generateUniqueCode()).thenReturn("ABC123");
	}

	private void mockSuccessfullySavedLoan() {
		when(loanRepository.save(ArgumentMatchers.any())).thenAnswer(i -> i.getArguments()[0]);
	}

	// CREATE LOAN

	// Positive test cases

	@Test
	void should_create_loan_successfully_for_board_game() throws Exception {
		mockLoggedUser();
		mockLoanCode();
		when(boardGameService.findById(createRequestBoardGame.getItemId())).thenReturn(boardGame);
		mockSuccessfullySavedLoan();

		LoanResponse response = loanService.createLoan(createRequestBoardGame);
        assertEquals(createRequestBoardGame.getItemId(), response.getItemId());
	}

	@Test
	void should_create_loan_successfully_for_book() throws Exception {
		mockLoggedUser();
		mockLoanCode();
		when(bookService.findById(createRequestBook.getItemId())).thenReturn(book);
		mockSuccessfullySavedLoan();

		LoanResponse response = loanService.createLoan(createRequestBook);
		assertEquals(createRequestBook.getItemId(), response.getItemId());
	}

	@Test
	void should_create_loan_successfully_for_movie() throws Exception {
		mockLoggedUser();
		mockLoanCode();
		when(movieService.findById(createRequestMovie.getItemId())).thenReturn(movie);
		mockSuccessfullySavedLoan();

		LoanResponse response = loanService.createLoan(createRequestMovie);
		assertEquals(createRequestMovie.getItemId(), response.getItemId());
	}

	@Test
	void should_create_loan_successfully_for_rol_game() throws Exception {
		mockLoggedUser();
		mockLoanCode();
		when(rolGameService.findById(createRequestRolGame.getItemId())).thenReturn(rolGame);
		mockSuccessfullySavedLoan();

		LoanResponse response = loanService.createLoan(createRequestRolGame);
		assertEquals(createRequestRolGame.getItemId(), response.getItemId());
	}

	@Test
	void should_create_loan_successfully_for_series() throws Exception {
		mockLoggedUser();
		mockLoanCode();
		when(seriesService.findById(createRequestSeries.getItemId())).thenReturn(series);
		mockSuccessfullySavedLoan();

		LoanResponse response = loanService.createLoan(createRequestSeries);
		assertEquals(createRequestSeries.getItemId(), response.getItemId());
	}

	// Negative test cases

	@Test
	void should_throw_validation_exception_when_requesting_videogame() {
		mockLoggedUser();

		assertThrows(ValidationException.class, () -> loanService.createLoan(createRequestVideoGame));
	}

	@Test
	void should_throw_field_exception_when_duplicate_loan_code_generated() throws Exception {
		mockLoggedUser();
		when(boardGameService.findById(createRequestBoardGame.getItemId())).thenReturn(boardGame);
		doThrow(DuplicateKeyException.class).when(loanRepository).save(ArgumentMatchers.any());

		FieldException ex = assertThrows(FieldException.class, () -> loanService.createLoan(createRequestBoardGame));
		assertTrue(ex.getErrors().containsKey("code"));
	}

	// - Not Found cases

	@Test
	void should_throw_not_found_exception_when_board_game_not_found() throws Exception {
		mockLoggedUser();
		when(boardGameService.findById(createRequestBoardGame.getItemId()))
				.thenThrow(ExceptionsFactory.notFoundException);

		assertThrows(NotFoundException.class, () -> loanService.createLoan(createRequestBoardGame));
	}

	@Test
	void should_throw_not_found_exception_when_book_not_found() throws Exception {
		mockLoggedUser();
		when(bookService.findById(createRequestBook.getItemId())).thenThrow(ExceptionsFactory.notFoundException);

		assertThrows(NotFoundException.class, () -> loanService.createLoan(createRequestBook));
	}

	@Test
	void should_throw_not_found_exception_when_movie_not_found() throws Exception {
		mockLoggedUser();
		when(movieService.findById(createRequestMovie.getItemId())).thenThrow(ExceptionsFactory.notFoundException);

		assertThrows(NotFoundException.class, () -> loanService.createLoan(createRequestMovie));
	}

	@Test
	void should_throw_not_found_exception_when_rol_game_not_found() throws Exception {
		mockLoggedUser();
		when(rolGameService.findById(createRequestRolGame.getItemId())).thenThrow(ExceptionsFactory.notFoundException);

		assertThrows(NotFoundException.class, () -> loanService.createLoan(createRequestRolGame));
	}

	@Test
	void should_throw_not_found_exception_when_series_not_found() throws Exception {
		mockLoggedUser();
		when(seriesService.findById(createRequestSeries.getItemId())).thenThrow(ExceptionsFactory.notFoundException);

		assertThrows(NotFoundException.class, () -> loanService.createLoan(createRequestSeries));
	}

	// - Validation cases

	@Test
	void should_throw_field_exception_when_board_game_validation_fails() throws Exception {
		mockLoggedUser();
		when(boardGameService.findById(createRequestBoardGame.getItemId())).thenReturn(boardGame);
		doThrow(ExceptionsFactory.fieldException(HttpStatus.CONFLICT, "itemId")).when(boardGameLoanSpecification)
				.validate(boardGame, "currentUserId");

		FieldException ex = assertThrows(FieldException.class, () -> loanService.createLoan(createRequestBoardGame));
		assertTrue(ex.getErrors().containsKey("itemId"));
	}

	@Test
	void should_throw_field_exception_when_book_validation_fails() throws Exception {
		mockLoggedUser();
		when(bookService.findById(createRequestBook.getItemId())).thenReturn(book);
		doThrow(ExceptionsFactory.fieldException(HttpStatus.CONFLICT, "itemId")).when(bookLoanSpecification)
				.validate(book, "currentUserId", ItemType.BOOK);

		FieldException ex = assertThrows(FieldException.class, () -> loanService.createLoan(createRequestBook));
		assertTrue(ex.getErrors().containsKey("itemId"));
	}

	@Test
	void should_throw_field_exception_when_movie_validation_fails() throws Exception {
		mockLoggedUser();
		when(movieService.findById(createRequestMovie.getItemId())).thenReturn(movie);
		doThrow(ExceptionsFactory.fieldException(HttpStatus.CONFLICT, "itemId")).when(movieLoanSpecification)
				.validate(movie, "currentUserId", ItemType.MOVIE);

		FieldException ex = assertThrows(FieldException.class, () -> loanService.createLoan(createRequestMovie));
		assertTrue(ex.getErrors().containsKey("itemId"));
	}

	@Test
	void should_throw_field_exception_when_rol_game_validation_fails() throws Exception {
		mockLoggedUser();
		when(rolGameService.findById(createRequestRolGame.getItemId())).thenReturn(rolGame);
		doThrow(ExceptionsFactory.fieldException(HttpStatus.CONFLICT, "itemId")).when(rolGameLoanSpecification)
				.validate(rolGame, "currentUserId");

		FieldException ex = assertThrows(FieldException.class, () -> loanService.createLoan(createRequestRolGame));
		assertTrue(ex.getErrors().containsKey("itemId"));
	}

	@Test
	void should_throw_field_exception_when_series_validation_fails() throws Exception {
		mockLoggedUser();
		when(seriesService.findById(createRequestSeries.getItemId())).thenReturn(series);
		doThrow(ExceptionsFactory.fieldException(HttpStatus.CONFLICT, "itemId")).when(seriesLoanSpecification)
				.validate(series, "currentUserId", ItemType.SERIES);

		FieldException ex = assertThrows(FieldException.class, () -> loanService.createLoan(createRequestSeries));
		assertTrue(ex.getErrors().containsKey("itemId"));
	}

}