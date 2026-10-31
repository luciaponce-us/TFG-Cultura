package com.tfg.cultura.api.loans.service.specifications;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.core.exception.FieldException;
import com.tfg.cultura.api.loans.model.Loan;
import com.tfg.cultura.api.loans.model.enumerators.LoanStatus;
import com.tfg.cultura.api.loans.repository.LoanRepository;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class LoanSpecificationTest {

	
	@Mock 
	private LoanRepository loanRepository;

	@InjectMocks 
	private LoanSpecification<Book> loanSpecification;

	private Book item;
	private ItemType itemType;

	@BeforeEach
	void setUp() {
		item = CatalogFactory.validBook();
		itemType = ItemType.BOOK;
	}

	@Test
	void should_do_nothing_when_item_is_available() {
		item.setLoanAvailable(true);
		loanSpecification.validateAvailability(item, itemType);
	}

	@Test
	void should_throw_field_exception_when_item_is_not_available() {
		item.setLoanAvailable(false);
		FieldException exception = assertThrows(FieldException.class,
				() -> loanSpecification.validateAvailability(item, itemType));

		assertTrue(exception.getErrors().containsKey("itemId"));
	}

	@Test
	void should_validate_successfully_when_item_is_available_and_has_no_active_loans() {
		when(loanRepository.findByUserIdAndStatusAndItemType("userId", LoanStatus.ACTIVE, ItemType.BOOK))
				.thenReturn(Set.of());

		loanSpecification.validate(item, "userId", ItemType.BOOK);

		verify(loanRepository).findByUserIdAndStatusAndItemType("userId", LoanStatus.ACTIVE, ItemType.BOOK);
	}

	@Test
	void should_not_query_active_loans_when_item_is_not_available() {
		item.setLoanAvailable(false);

		assertThrows(FieldException.class, () -> loanSpecification.validate(item, "userId", ItemType.BOOK));

		verifyNoInteractions(loanRepository);
	}

	@Test
	void should_throw_conflict_when_book_has_an_active_loan() {
		assertActiveLoanError(ItemType.BOOK,
				"No se puede solicitar un préstamo de varios libros concurrentemente.");
	}

	@Test
	void should_throw_conflict_when_movie_has_an_active_loan() {
		assertActiveLoanError(ItemType.MOVIE,
				"No se puede solicitar un préstamo de varias películas concurrentemente.");
	}

	@Test
	void should_throw_conflict_when_series_has_an_active_loan() {
		assertActiveLoanError(ItemType.SERIES,
				"No se puede solicitar un préstamo de varias series concurrentemente.");
	}

	@Test
	void should_reject_active_loan_validation_for_unsupported_item_type() {
		when(loanRepository.findByUserIdAndStatusAndItemType("userId", LoanStatus.ACTIVE, ItemType.BOARDGAME))
				.thenReturn(Set.of(new Loan()));

		IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
				() -> loanSpecification.validate(item, "userId", ItemType.BOARDGAME));

		assertEquals("El tipo de artículo no admite esta especificación: BOARDGAME", exception.getMessage());
	}

	private void assertActiveLoanError(ItemType type, String expectedMessage) {
		when(loanRepository.findByUserIdAndStatusAndItemType("userId", LoanStatus.ACTIVE, type))
				.thenReturn(Set.of(new Loan()));

		FieldException exception = assertThrows(FieldException.class,
				() -> loanSpecification.validate(item, "userId", type));

		assertEquals(expectedMessage, exception.getErrors().get("itemId"));
	}
}
