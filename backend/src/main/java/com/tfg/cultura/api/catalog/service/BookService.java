package com.tfg.cultura.api.catalog.service;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

import com.tfg.cultura.api.catalog.exception.item.ItemAlreadyExistsException;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.dto.BookRequest;
import com.tfg.cultura.api.catalog.model.dto.BookResponse;
import com.tfg.cultura.api.catalog.repository.BookRepository;
import com.tfg.cultura.api.categories.service.CategoryService;
import com.tfg.cultura.api.core.config.AppProperties;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.service.SectionService;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class BookService extends AbstractItemService<Book, BookRepository, BookRequest, BookResponse> {

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
		Saga saga = sagaService.findByName(request.getSagaName());

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

}
