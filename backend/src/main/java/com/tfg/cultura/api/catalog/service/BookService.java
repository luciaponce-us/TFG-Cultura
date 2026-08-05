package com.tfg.cultura.api.catalog.service;

import org.springframework.stereotype.Service;

import com.tfg.cultura.api.catalog.exception.item.ItemAlreadyExistsException;
import com.tfg.cultura.api.catalog.exception.item.ItemNotFoundException;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.dto.BookCreateRequest;
import com.tfg.cultura.api.catalog.model.dto.BookResponse;
import com.tfg.cultura.api.catalog.repository.BookRepository;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.service.SectionService;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

@Service
public class BookService extends AbstractItemService<Book, BookRepository, BookCreateRequest, BookResponse> {

    private final SagaService sagaService;

    public BookService(BookRepository bookRepository, SectionService sectionService, CategoryService categoryService,
            FileService fileService, SagaService sagaService) {
        super(bookRepository, sectionService, categoryService, fileService, BookResponse::new);
        this.sagaService = sagaService;
    }

    @Override
    protected String getImageFolder() {
        return "cultura/items/books";
    }

    @Override
    protected String getDefaultImageUrl() {
        return "https://res.cloudinary.com/dubz79y98/image/upload/v1785682202/book_placeholder.jpg";
    }

    @Override
    protected void validate(Book item) {
        checkUniqueIsbn(item);
    }

    private void checkUniqueIsbn(Book item) throws ItemAlreadyExistsException {
        String isbn = item.getIsbn();
        if (isbn != null && repository.existsByIsbn(isbn)) {
            throw new ItemAlreadyExistsException("El ISBN ya existe en otro libro. ISBN: " + sanitize(isbn));
        }
    }

    @Override
    protected Book createEntity() {
        return Book.builder().build();
    }

    @Override
    protected void fillSpecificFields(Book item, BookCreateRequest request) {
        Saga saga = sagaService.findByName(request.getSaga());

        item.setAuthor(sanitize(request.getAuthor()));
        item.setIsbn(sanitize(request.getIsbn()));
        item.setType(request.getType());
        item.setSaga(saga);
    }

    @Override
    protected Integer getLoanDays(BookCreateRequest request) {
        switch (request.getType()) {
            case NOVEL:
                return 15; // RN-15
            case COMIC, MANGA:
                return 7; // RN-16
            case ROL:
                return 15; // RN-17
            default:
                return 15;
        }
    }

    @Override
    protected void postCreationActions(Book item) {
        // No additional actions needed after creation
    }

    @Override
    protected void preDeletionActions(Book item) {
        // No additional actions needed before deletion
    }

    @Override
    protected void postUpdateActions(Book oldItem, Book updatedItem)
            throws ItemNotFoundException, IllegalArgumentException {
        // No additional actions needed after update
    }

}
