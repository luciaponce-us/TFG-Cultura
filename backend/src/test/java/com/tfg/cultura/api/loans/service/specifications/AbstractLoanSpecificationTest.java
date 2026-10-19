package com.tfg.cultura.api.loans.service.specifications;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.enumerators.ItemType;
import com.tfg.cultura.api.core.exception.FieldException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.LoggerFactory;

@ExtendWith(MockitoExtension.class)
class AbstractLoanSpecificationTest {

	private AbstractLoanSpecification<Book> loanSpecification;

	private Book item;
	private ItemType itemType;

	@BeforeEach
	void setUp() {
		item = CatalogFactory.validBook();
		itemType = ItemType.BOOK;
		loanSpecification = new AbstractLoanSpecification<Book>(LoggerFactory.getLogger("testLogger")) {
			@Override
			public void validate(Book item, String userId) {
				// This test targets the shared availability validation.
			}
		};
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
}
