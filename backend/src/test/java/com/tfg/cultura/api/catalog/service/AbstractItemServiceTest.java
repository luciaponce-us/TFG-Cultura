package com.tfg.cultura.api.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.tfg.cultura.api.catalog.exception.item.ItemNotFoundException;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.dto.BookRequest;
import com.tfg.cultura.api.catalog.model.dto.BookResponse;
import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.catalog.repository.BookRepository;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.categories.service.CategoryService;
import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.factory.AppPropertiesFactory;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.model.dto.SectionReference;
import com.tfg.cultura.api.sections.service.SectionService;

@ExtendWith(MockitoExtension.class)
class AbstractItemServiceTest {
	@Mock
	private BookRepository repository;
	@Mock
	private SectionService sectionService;
	@Mock
	private CategoryService categoryService;
	@Mock
	private FileService fileService;
	@Mock
	private SagaService sagaService;

	private BookService service;

	private BookRequest request;
	private Section section;
	private Category category;
	private Saga saga;

	@BeforeEach
	void setUp() {
		AppProperties appProperties = AppPropertiesFactory.validAppProperties();
		service = new BookService(
				repository,
				sectionService,
				categoryService,
				fileService,
				sagaService,
				appProperties);

		section = Section.builder().id("section").build();
		category = Category.builder().id("category").build();
		saga = Saga.builder().name("Harry Potter").build();

		request = BookRequest.builder()
				.name("Harry Potter")
				.description("...")
				.author("J.K. Rowling")
				.isbn("9781234567897")
				.type(BookType.NOVEL)
				.sectionId("section")
				.categoriesIds(Set.of("category"))
				.copies(2)
				.availableCopies(2)
				.loanAvailable(true)
				.sagaName("Harry Potter")
				.build();
	}

	private void mockFileServiceUploadImage(String imageUrl) {
		when(fileService.uploadImage(
				any(),
				any(),
				any(),
				any(),
				any(),
				anyInt(),
				anyInt()))
				.thenReturn(imageUrl);
	}

	private void mockFileServiceUpdateImage(String imageUrl) {
		when(fileService.updateImage(
				anyString(),
				anyString(),
				anyString(),
				any(),
				anyString(),
				anyString(),
				anyInt(),
				anyInt()))
				.thenReturn(imageUrl);
	}

	// CREATE

	@Test
	void should_create_item_successfully() {

		when(sectionService.findSectionById("section")).thenReturn(section);
		when(categoryService.findCategoriesByIds(any())).thenReturn(Set.of(category));
		when(sagaService.findByName("Harry Potter")).thenReturn(saga);
		when(repository.existsByIsbn(any())).thenReturn(false);
		mockFileServiceUploadImage(service.getDefaultImageUrl());

		when(repository.save(any(Book.class)))
				.thenAnswer(inv -> {
					Book b = inv.getArgument(0);
					b.setId("1");
					return b;
				});

		BookResponse response = service.create(request, null);

		assertEquals("Harry Potter", response.getName());

		ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);

		verify(repository, times(2)).save(captor.capture());

		assertEquals("Harry Potter", response.getName());
		assertEquals("J.K. Rowling", response.getAuthor());
		SectionReference sectionRef = new SectionReference(section);
		assertEquals(sectionRef.getId(), response.getSection().getId());
		assertEquals(saga.getName(), response.getSaga());
		assertEquals(15, response.getLoanDays());
		assertEquals(service.getDefaultImageUrl(), response.getImageUrl());
	}

	@Test
	void should_throw_exception_when_available_copies_greater_than_total_copies() {
		request.setAvailableCopies(3);
		request.setCopies(2);

		assertThrows(IllegalArgumentException.class, () -> service.create(request, null));
	}

	@Test
	void should_throw_exception_when_available_copies_less_than_zero() {
		request.setAvailableCopies(-1);

		assertThrows(IllegalArgumentException.class, () -> service.create(request, null));
	}

	// READ

	@Test
	void should_return_book_when_book_exists() {
		Book book = Book.builder()
				.id("book-id")
				.name("Harry Potter")
				.build();

		when(repository.findById("book-id"))
				.thenReturn(Optional.of(book));

		Book result = service.findById("book-id");

		assertEquals(book, result);
		verify(repository).findById("book-id");
	}

	@Test
	void should_throw_when_book_does_not_exist() {
		when(repository.findById("book-id"))
				.thenReturn(Optional.empty());

		ItemNotFoundException exception = assertThrows(
				ItemNotFoundException.class,
				() -> service.findById("book-id"));

		assertEquals(
				"Item no encontrado con ID: book-id",
				exception.getMessage());

		verify(repository).findById("book-id");
	}

	@Test
	void should_return_book_response_when_book_exists() {

		Book book = Book.builder()
				.id("1")
				.name("Harry Potter")
				.author("J.K. Rowling")
				.build();

		when(repository.findById("1"))
				.thenReturn(Optional.of(book));

		BookResponse response = service.getById("1");

		assertEquals("1", response.getId());
		assertEquals("Harry Potter", response.getName());
		assertEquals("J.K. Rowling", response.getAuthor());

		verify(repository).findById("1");
	}

	@Test
	void should_throw_when_getting_non_existing_book() {

		when(repository.findById("1"))
				.thenReturn(Optional.empty());

		assertThrows(ItemNotFoundException.class,
				() -> service.getById("1"));
	}

	@Test
	void should_return_all_books() {

		Book book1 = Book.builder().id("1").name("Book 1").build();
		Book book2 = Book.builder().id("2").name("Book 2").build();

		PageRequest pageable = PageRequest.of(0, 10);

		Page<Book> page = new PageImpl<>(
				List.of(book1, book2),
				pageable,
				2);

		when(repository.findAll(pageable))
				.thenReturn(page);

		Page<BookResponse> result = service.getAll(pageable, null, null);

		assertEquals(2, result.getTotalElements());

		assertEquals("Book 1", result.getContent().get(0).getName());
		assertEquals("Book 2", result.getContent().get(1).getName());

		verify(repository).findAll(pageable);
	}

	// UPDATE
	@Test
	void should_update_book() {

		Book book = Book.builder()
				.id("1")
				.imageUrl(service.getDefaultImageUrl())
				.build();

		when(repository.findById("1"))
				.thenReturn(Optional.of(book));

		when(sectionService.findSectionById(any()))
				.thenReturn(section);

		when(categoryService.findCategoriesByIds(any()))
				.thenReturn(Set.of(category));

		when(sagaService.findByName(any()))
				.thenReturn(saga);

		when(repository.existsByIsbn(any()))
				.thenReturn(false);

		when(repository.save(any(Book.class)))
				.thenAnswer(inv -> inv.getArgument(0));

		BookResponse response = service.update("1", request, null);

		assertEquals(request.getName(), response.getName());

		verify(repository, times(2)).save(book);
	}

	@Test
	void should_throw_when_updating_non_existing_book() {
		when(repository.findById("1"))
				.thenReturn(Optional.empty());

		assertThrows(ItemNotFoundException.class,
				() -> service.update("1", request, null));
	}

	@Test
	void should_update_book_image() {
		Book book = Book.builder()
				.id("1")
				.imageUrl(service.getDefaultImageUrl())
				.build();

		when(repository.findById("1"))
				.thenReturn(Optional.of(book));

		when(sectionService.findSectionById(any()))
				.thenReturn(section);

		when(categoryService.findCategoriesByIds(any()))
				.thenReturn(Set.of(category));

		when(sagaService.findByName(any()))
				.thenReturn(saga);

		when(repository.existsByIsbn(any()))
				.thenReturn(false);

		when(repository.save(any(Book.class)))
				.thenAnswer(inv -> inv.getArgument(0));

		MockMultipartFile image = new MockMultipartFile(
				"image",
				"book.jpg",
				MediaType.IMAGE_JPEG_VALUE,
				"data".getBytes());

		mockFileServiceUpdateImage("https://cloudinary/...");

		BookResponse response = service.update("1", request, image);

		assertEquals("https://cloudinary/...", response.getImageUrl());

		verify(fileService).updateImage(anyString(), anyString(), anyString(), any(), anyString(), anyString(),
				anyInt(), anyInt());
	}

	// DELETE

	@Test
	void should_delete_existing_book() {
		Book book = Book.builder()
				.id("1")
				.build();

		when(repository.findById("1"))
				.thenReturn(Optional.of(book));

		service.delete("1");

		verify(repository).findById("1");
		verify(repository).delete(book);
	}

	@Test
	void should_throw_when_deleting_non_existing_book() {

		when(repository.findById("1"))
				.thenReturn(Optional.empty());

		assertThrows(
				ItemNotFoundException.class,
				() -> service.delete("1"));

		verify(repository).findById("1");
		verify(repository, never()).delete(any());
	}

	@Test
	void should_remove_category_from_all_books() {

		Category category = Category.builder()
				.id("category-1")
				.name("Fantasy")
				.build();

		Category anotherCategory = Category.builder()
				.id("category-2")
				.name("Novel")
				.build();

		Book book1 = Book.builder()
				.categories(new HashSet<>(Set.of(category, anotherCategory)))
				.build();

		Book book2 = Book.builder()
				.categories(new HashSet<>(Set.of(category)))
				.build();

		when(repository.findAllByCategoriesContaining(category))
				.thenReturn(List.of(book1, book2));

		service.removeCategory(category);

		assertFalse(book1.getCategories().contains(category));
		assertTrue(book1.getCategories().contains(anotherCategory));

		assertFalse(book2.getCategories().contains(category));

		verify(repository).findAllByCategoriesContaining(category);
		verify(repository).save(book1);
		verify(repository).save(book2);
	}

	@Test
	void should_do_nothing_when_no_books_have_category() {

		Category category = Category.builder().build();

		when(repository.findAllByCategoriesContaining(category))
				.thenReturn(List.of());

		service.removeCategory(category);

		verify(repository).findAllByCategoriesContaining(category);
		verify(repository, never()).save(any());
	}

}
