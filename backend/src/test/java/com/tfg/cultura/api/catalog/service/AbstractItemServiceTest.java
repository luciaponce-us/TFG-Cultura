package com.tfg.cultura.api.catalog.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.dto.BookRequest;
import com.tfg.cultura.api.catalog.model.dto.BookResponse;
import com.tfg.cultura.api.catalog.repository.BookRepository;
import com.tfg.cultura.api.categories.factory.CategoryFactory;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.categories.service.CategoryService;
import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.exception.NotFoundException;
import com.tfg.cultura.api.core.factory.AppPropertiesFactory;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.factory.SectionFactory;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.model.dto.SectionReference;
import com.tfg.cultura.api.sections.service.SectionService;
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

	private Book book;
	private BookRequest request;
	private Section section;
	private Category category;
	private Category anotherCategory;
	private Saga saga;

	@BeforeEach
	void setUp() {
		AppProperties appProperties = AppPropertiesFactory.validAppProperties();
		service = new BookService(repository, sectionService, categoryService, fileService, sagaService, appProperties);

		section = SectionFactory.validSection();
		category = CategoryFactory.validCategory();
		anotherCategory = CategoryFactory.anotherValidCategory();
		saga = CatalogFactory.validSaga();
		book = CatalogFactory.validBookWithSaga("1", saga);
		book.setImageUrl(service.getDefaultImageUrl());
		request = CatalogFactory.validBookRequest();
	}

	private void mockFileServiceUpdateImage(String imageUrl) {
		when(fileService.updateImage(any(), any(), any())).thenReturn(imageUrl);
	}

	// CREATE

	@Test
	void should_create_item_successfully() {
		request.setSectionId(section.getId());
		request.setSagaName(saga.getName());

		when(sectionService.findSectionById(section.getId())).thenReturn(section);
		when(categoryService.findCategoriesByIds(any())).thenReturn(Set.of(category));
		when(sagaService.findByName(saga.getName())).thenReturn(saga);
		when(repository.existsByIsbn(any())).thenReturn(false);

		when(repository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

		BookResponse response = service.create(request, null);

		assertEquals(book.getName(), response.getName());

		ArgumentCaptor<Book> captor = ArgumentCaptor.forClass(Book.class);

		verify(repository).save(captor.capture());

		assertEquals(book.getName(), response.getName());
		assertEquals(book.getAuthor(), response.getAuthor());
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
		when(repository.findById(book.getId())).thenReturn(Optional.of(book));

		Book result = service.findById(book.getId());

		assertEquals(book, result);
		verify(repository).findById(book.getId());
	}

	@Test
	void should_throw_when_book_does_not_exist() {
		when(repository.findById("book-id")).thenReturn(Optional.empty());

		NotFoundException exception = assertThrows(NotFoundException.class, () -> service.findById("book-id"));

		assertEquals("Item no encontrado con ID: book-id", exception.getMessage());

		verify(repository).findById("book-id");
	}

	@Test
	void should_return_book_response_when_book_exists() {
		when(repository.findById(book.getId())).thenReturn(Optional.of(book));

		BookResponse response = service.getById(book.getId());

		assertEquals(book.getId(), response.getId());
		assertEquals(book.getName(), response.getName());
		assertEquals(book.getAuthor(), response.getAuthor());

		verify(repository).findById(book.getId());
	}

	@Test
	void should_throw_when_getting_non_existing_book() {

		when(repository.findById("1")).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> service.getById("1"));
	}

	@Test
	void should_return_all_books() {

		Book book2 = Book.builder().id("2").name("Book 2").build();

		PageRequest pageable = PageRequest.of(0, 10);

		Page<Book> page = new PageImpl<>(List.of(book, book2), pageable, 2);

		when(repository.findAll(pageable)).thenReturn(page);

		Page<BookResponse> result = service.getAll(pageable, null, null);

		assertEquals(2, result.getTotalElements());

		assertEquals(book.getName(), result.getContent().get(0).getName());
		assertEquals("Book 2", result.getContent().get(1).getName());

		verify(repository).findAll(pageable);
	}

	// UPDATE
	@Test
	void should_update_book() {
		when(repository.findById(book.getId())).thenReturn(Optional.of(book));
		when(sectionService.findSectionById(any())).thenReturn(section);
		when(categoryService.findCategoriesByIds(any())).thenReturn(Set.of(category));
		when(repository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

		BookResponse response = service.update(book.getId(), request, null);

		assertEquals(request.getName(), response.getName());

		verify(repository).save(book);
	}

	@Test
	void should_throw_when_updating_non_existing_book() {
		when(repository.findById("1")).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> service.update("1", request, null));
	}

	@Test
	void should_update_book_image() {
		when(repository.findById("1")).thenReturn(Optional.of(book));
		when(sectionService.findSectionById(any())).thenReturn(section);
		when(categoryService.findCategoriesByIds(any())).thenReturn(Set.of(category));

		when(repository.save(any(Book.class))).thenAnswer(inv -> inv.getArgument(0));

		MockMultipartFile image = new MockMultipartFile("image", "book.jpg", MediaType.IMAGE_JPEG_VALUE,
				"data".getBytes());

		mockFileServiceUpdateImage("https://cloudinary/...");

		BookResponse response = service.update("1", request, image);

		assertEquals("https://cloudinary/...", response.getImageUrl());

		verify(fileService).updateImage(any(), any(), any());
	}

	// DELETE

	@Test
	void should_delete_existing_book() {
		when(repository.findById(book.getId())).thenReturn(Optional.of(book));

		service.delete(book.getId());

		verify(repository).findById(book.getId());
		verify(repository).delete(book);
	}

	@Test
	void should_throw_when_deleting_non_existing_book() {
		when(repository.findById("1")).thenReturn(Optional.empty());

		assertThrows(NotFoundException.class, () -> service.delete("1"));

		verify(repository).findById("1");
		verify(repository, never()).delete(any());
	}

	@Test
	void should_remove_category_from_all_books() {
		book.setCategories(new HashSet<Category>(Set.of(category, anotherCategory)));

		Book book2 = Book.builder().id("2").name("Book 2").categories(new HashSet<Category>(Set.of(category))).build();

		when(repository.findAllByCategoriesContaining(category)).thenReturn(List.of(book, book2));

		service.removeCategory(category);

		assertFalse(book.getCategories().contains(category));
		assertTrue(book.getCategories().contains(anotherCategory));

		assertFalse(book2.getCategories().contains(category));

		verify(repository).findAllByCategoriesContaining(category);
		verify(repository).save(book);
		verify(repository).save(book2);
	}

	@Test
	void should_do_nothing_when_no_books_have_category() {
		when(repository.findAllByCategoriesContaining(category)).thenReturn(List.of());

		service.removeCategory(category);

		verify(repository).findAllByCategoriesContaining(category);
		verify(repository, never()).save(any());
	}

}
