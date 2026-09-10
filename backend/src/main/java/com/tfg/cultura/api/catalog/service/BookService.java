package com.tfg.cultura.api.catalog.service;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

import com.tfg.cultura.api.catalog.exception.item.ItemAlreadyExistsException;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.dto.BookRequest;
import com.tfg.cultura.api.catalog.model.dto.BookResponse;
import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.catalog.repository.BookRepository;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.categories.service.CategoryService;
import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.service.SectionService;
import java.util.Map;
import java.util.Set;
import org.apache.logging.log4j.internal.annotation.SuppressFBWarnings;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class BookService extends AbstractItemService<Book, BookRepository, BookRequest, BookResponse> {

	@SuppressFBWarnings(value = "EI_EXPOSE_REP2", justification = "Spring dependency injection")
	private final SagaService sagaService;

	private final AppProperties appProperties;

	public BookService(BookRepository bookRepository, SectionService sectionService, CategoryService categoryService,
			FileService fileService, SagaService sagaService, AppProperties appProperties) {
		super(bookRepository, sectionService, categoryService, fileService, BookResponse::new);
		this.sagaService = sagaService;
		this.appProperties = appProperties;
	}

	@Override
	protected String getImageFolder() {
		return "cultura/items/books";
	}

	@Override
	protected String getDefaultImageUrl() {
		return appProperties.defaultImages().book();
	}

	@Override
	protected void validate(Book item) {
		checkUniqueIsbn(item);
	}

	private void checkUniqueIsbn(Book item) throws ItemAlreadyExistsException {
		String isbn = item.getIsbn();
		if (isbn != null && repository.existsByIsbn(isbn)) {
			throw new ItemAlreadyExistsException(
					Map.of("isbn", "El ISBN " + sanitize(isbn) + " ya existe en otro libro."));
		}
	}

	@Override
	protected Book createEntity() {
		return Book.builder().build();
	}

	@Override
	protected void fillSpecificFields(Book item, BookRequest request) {
		String sagaName = request.getSagaName();
		Saga saga = sagaName == null || sagaName.isBlank() ? null : sagaService.findByName(sagaName);

		item.setAuthor(sanitize(request.getAuthor()));
		item.setIsbn(sanitize(request.getIsbn()));
		item.setType(request.getType());
		item.setSaga(saga);
	}

	@Override
	protected Integer getLoanDays(BookRequest request) {
		switch (request.getType()) {
			case NOVEL :
				return 15; // RN-15
			case COMIC, MANGA :
				return 7; // RN-16
			case ROL :
				return 15; // RN-17
			default :
				return 15;
		}
	}

	public Page<BookResponse> getAllBooksByTypeAndNameContains(Set<BookType> types, String nameContains,
			Set<String> categoryIds, Pageable pageable) {
		Set<Category> categories = categoryIds == null || categoryIds.isEmpty()
				? null
				: categoryService.findCategoriesByIds(categoryIds);

		Page<Book> books;
		if (nameContains == null || nameContains.isBlank()) {
			books = categories == null
					? repository.findAllByTypeIn(types, pageable)
					: repository.findAllByTypeInAndCategoriesContaining(types, categories, pageable);
		} else {
			books = categories == null
					? repository.findAllByTypeInAndNameContainingIgnoreCase(types, nameContains, pageable)
					: repository.findAllByTypeInAndNameContainingIgnoreCaseAndCategoriesContaining(types, nameContains,
							categories, pageable);
		}
		return books.map(BookResponse::new);
	}

}
