package com.tfg.cultura.api.catalog.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anySet;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Set;

import com.tfg.cultura.api.catalog.exception.item.ItemAlreadyExistsException;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.dto.BookRequest;
import com.tfg.cultura.api.catalog.model.dto.BookResponse;
import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.catalog.repository.BookRepository;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.categories.service.CategoryService;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.service.SectionService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

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
		Book book = Book.builder().isbn("9781234567890").build();

		when(bookRepository.existsByIsbn("9781234567890")).thenReturn(true);

		assertThrows(ItemAlreadyExistsException.class, () -> service.validate(book));
	}

	@Test
	void should_not_throw_when_isbn_is_unique() {
		Book book = Book.builder().isbn("9781234567890").build();

		when(bookRepository.existsByIsbn(any())).thenReturn(false);

		assertDoesNotThrow(() -> service.validate(book));
	}

	@Test
	void should_fill_book_specific_fields() {

		Saga saga = new Saga();

		BookRequest request = BookRequest.builder().author("Asimov").isbn("978...").type(BookType.NOVEL)
				.sagaName("Fundación").build();

		when(sagaService.findByName("Fundación")).thenReturn(saga);

		Book book = new Book();

		service.fillSpecificFields(book, request);

		assertEquals("Asimov", book.getAuthor());
		assertEquals("978...", book.getIsbn());
		assertEquals(BookType.NOVEL, book.getType());
		assertEquals(saga, book.getSaga());

		verify(sagaService).findByName("Fundación");
	}

	@Test
	void should_allow_book_without_saga() {
		BookRequest request = BookRequest.builder().author("Asimov").isbn("978...").type(BookType.NOVEL).build();
		Book book = new Book();

		service.fillSpecificFields(book, request);

		assertNull(book.getSaga());
		verifyNoInteractions(sagaService);
	}

	@Test
	void should_return_15_days_for_novel() {
		BookRequest request = BookRequest.builder().type(BookType.NOVEL).build();

		assertEquals(15, service.getLoanDays(request));
	}

	@Test
	void should_return_15_days_for_comic() {
		BookRequest request = BookRequest.builder().type(BookType.COMIC).build();

		assertEquals(7, service.getLoanDays(request));
	}

	@Test
	void should_return_15_days_for_manga() {
		BookRequest request = BookRequest.builder().type(BookType.MANGA).build();

		assertEquals(7, service.getLoanDays(request));
	}

	private final PageRequest pageable = PageRequest.of(0, 10);

	@Test
	void should_find_books_by_type_when_name_and_categories_are_empty() {
		Set<BookType> types = Set.of(BookType.NOVEL);
		Page<Book> books = new PageImpl<>(List.of(new Book()));

		when(bookRepository.findAllByTypeIn(types, pageable)).thenReturn(books);

		Page<BookResponse> result = service.getAllBooksByTypeAndNameContains(
				types, null, null, pageable);

		assertEquals(books.getTotalElements(), result.getTotalElements());
		verify(bookRepository).findAllByTypeIn(types, pageable);
		verify(bookRepository, never()).findAllByTypeInAndCategoriesContaining(
				anySet(), anySet(), any());
		verifyNoInteractions(categoryService);
	}

	@Test
	void should_find_books_by_type_and_categories_when_name_is_empty() {
		Set<BookType> types = Set.of(BookType.NOVEL);
		Set<String> categoryIds = Set.of("category-1", "category-2");
		Set<Category> categories = Set.of(new Category(), new Category());
		Page<Book> books = new PageImpl<>(List.of(new Book()));

		when(categoryService.findCategoriesByIds(categoryIds)).thenReturn(categories);
		when(bookRepository.findAllByTypeInAndCategoriesContaining(
				types, categories, pageable)).thenReturn(books);

		Page<BookResponse> result = service.getAllBooksByTypeAndNameContains(
				types, "", categoryIds, pageable);

		assertEquals(books.getTotalElements(), result.getTotalElements());

		verify(categoryService).findCategoriesByIds(categoryIds);
		verify(bookRepository).findAllByTypeInAndCategoriesContaining(
				types, categories, pageable);
		verify(bookRepository, never()).findAllByTypeIn(anySet(), any());
	}

	@Test
	void should_find_books_by_type_and_name_when_categories_are_empty() {
		Set<BookType> types = Set.of(BookType.NOVEL);
		String nameContains = "harry";
		Page<Book> books = new PageImpl<>(List.of(new Book()));

		when(bookRepository.findAllByTypeInAndNameContainingIgnoreCase(
				types, nameContains, pageable)).thenReturn(books);

		Page<BookResponse> result = service.getAllBooksByTypeAndNameContains(
				types, nameContains, null, pageable);

		assertEquals(books.getTotalElements(), result.getTotalElements());

		verify(bookRepository).findAllByTypeInAndNameContainingIgnoreCase(
				types, nameContains, pageable);
		verify(bookRepository, never()).findAllByTypeIn(anySet(), any());
		verify(bookRepository, never()).findAllByTypeInAndCategoriesContaining(
				anySet(), anySet(), any());
		verifyNoInteractions(categoryService);
	}

	@Test
	void should_find_books_by_type_name_and_categories() {
		Set<BookType> types = Set.of(BookType.NOVEL);
		String nameContains = "harry";
		Set<String> categoryIds = Set.of("category-1", "category-2");
		Set<Category> categories = Set.of(new Category(), new Category());
		Page<Book> books = new PageImpl<>(List.of(new Book()));

		when(categoryService.findCategoriesByIds(categoryIds)).thenReturn(categories);
		when(bookRepository.findAllByTypeInAndNameContainingIgnoreCaseAndCategoriesContaining(
				types, nameContains, categories, pageable)).thenReturn(books);

		Page<BookResponse> result = service.getAllBooksByTypeAndNameContains(
				types, nameContains, categoryIds, pageable);

		assertEquals(books.getTotalElements(), result.getTotalElements());

		verify(categoryService).findCategoriesByIds(categoryIds);
		verify(bookRepository).findAllByTypeInAndNameContainingIgnoreCaseAndCategoriesContaining(
				types, nameContains, categories, pageable);
		verify(bookRepository, never()).findAllByTypeIn(anySet(), any());
		verify(bookRepository, never()).findAllByTypeInAndCategoriesContaining(
				anySet(), anySet(), any());
		verify(bookRepository, never()).findAllByTypeInAndNameContainingIgnoreCase(
				anySet(), anyString(), any());
	}

}
