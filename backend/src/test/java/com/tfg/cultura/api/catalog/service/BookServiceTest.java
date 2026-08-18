package com.tfg.cultura.api.catalog.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.cultura.api.catalog.exception.item.ItemAlreadyExistsException;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.dto.BookRequest;
import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.catalog.repository.BookRepository;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.service.SectionService;

@ExtendWith(MockitoExtension.class)
class BookServiceTest {

	@Mock
	private BookRepository bookRepository;

	@Mock
	private SectionService sectionService;

	@Mock
	private CategoryService categoryService;

	@Mock
	private FileService fileService;

	@Mock
	private SagaService sagaService;

	@InjectMocks
	private BookService service;

	@Test
	void should_throw_exception_when_isbn_already_exists() {
		Book book = Book.builder()
				.isbn("9781234567890")
				.build();

		when(bookRepository.existsByIsbn("9781234567890"))
				.thenReturn(true);

		assertThrows(ItemAlreadyExistsException.class,
				() -> service.validate(book));
	}

	@Test
	void should_not_throw_when_isbn_is_unique() {
		Book book = Book.builder()
				.isbn("9781234567890")
				.build();

		when(bookRepository.existsByIsbn(any()))
				.thenReturn(false);

		assertDoesNotThrow(() -> service.validate(book));
	}

	@Test
	void should_fill_book_specific_fields() {

		Saga saga = new Saga();

		BookRequest request = BookRequest.builder()
				.author("Asimov")
				.isbn("978...")
				.type(BookType.NOVEL)
				.sagaName("Fundación")
				.build();

		when(sagaService.findByName("Fundación"))
				.thenReturn(saga);

		Book book = new Book();

		service.fillSpecificFields(book, request);

		assertEquals("Asimov", book.getAuthor());
		assertEquals("978...", book.getIsbn());
		assertEquals(BookType.NOVEL, book.getType());
		assertEquals(saga, book.getSaga());

		verify(sagaService).findByName("Fundación");
	}

	@Test
	void should_return_15_days_for_novel() {
		BookRequest request = BookRequest.builder()
				.type(BookType.NOVEL)
				.build();

		assertEquals(15, service.getLoanDays(request));
	}

	@Test
	void should_return_15_days_for_comic() {
		BookRequest request = BookRequest.builder()
				.type(BookType.COMIC)
				.build();

		assertEquals(7, service.getLoanDays(request));
	}

	@Test
	void should_return_15_days_for_manga() {
		BookRequest request = BookRequest.builder()
				.type(BookType.MANGA)
				.build();

		assertEquals(7, service.getLoanDays(request));
	}

	@Test
	void should_return_15_days_for_rol() {
		BookRequest request = BookRequest.builder()
				.type(BookType.ROL)
				.build();

		assertEquals(15, service.getLoanDays(request));
	}

}
