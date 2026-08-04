package com.tfg.cultura.api.catalog.service;

import org.springframework.stereotype.Service;

import com.tfg.cultura.api.catalog.exception.DuplicateItemNumberInSagaException;
import com.tfg.cultura.api.catalog.exception.ItemAlreadyExistsException;
import com.tfg.cultura.api.catalog.exception.ItemCannotBeItsOwnPrequelException;
import com.tfg.cultura.api.catalog.exception.ItemCannotBeItsOwnSequelException;
import com.tfg.cultura.api.catalog.exception.ItemNotFoundException;
import com.tfg.cultura.api.catalog.exception.SagaFieldsMustBeEmptyIfNotSagaException;
import com.tfg.cultura.api.catalog.exception.SamePrequelAndSequelException;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.dto.BookCreateRequest;
import com.tfg.cultura.api.catalog.model.dto.BookResponse;
import com.tfg.cultura.api.catalog.repository.BookRepository;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.service.SectionService;

import static com.tfg.cultura.api.core.utils.LoggerSanitizer.sanitize;

@Service
public class BookService extends AbstractItemService<Book, BookRepository, BookCreateRequest, BookResponse> {

    public BookService(BookRepository bookRepository, SectionService sectionService, CategoryService categoryService,
            FileService fileService) {
        super(bookRepository, sectionService, categoryService, fileService, BookResponse::new);
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
        checkPrequelIsNotSameAsSequel(item);
        checkBookIsNotItsOwnSequel(item);
        checkBookIsNotItsOwnPrequel(item);
        checkUniqueIsbn(item);
        checkSagaFieldsMustBeEmptyIfNotSaga(item);
        checkUniqueItemNumberInSaga(item);
        return;
    }

    private void checkPrequelIsNotSameAsSequel(Book item) {
        String prequelId = item.getPrequel() != null ? item.getPrequel().getId() : null;
        String sequelId = item.getSequel() != null ? item.getSequel().getId() : null;
        if (prequelId != null && sequelId != null && prequelId.equals(sequelId)) {
            throw new SamePrequelAndSequelException(
                    "El libro no puede tener el mismo predecesor y sucesor. Predecesor ID: " + sanitize(prequelId)
                            + ", Sucesor ID: " + sanitize(sequelId));
        }
    }

    private void checkBookIsNotItsOwnSequel(Book item) {
        String bookId = item.getId();
        String sequelId = item.getSequel() != null ? item.getSequel().getId() : null;
        if (bookId != null && sequelId != null && bookId.equals(sequelId)) {
            throw new ItemCannotBeItsOwnSequelException(
                    "El libro no puede ser su propio sucesor. Libro ID: " + sanitize(bookId));
        }
    }

    private void checkBookIsNotItsOwnPrequel(Book item) {
        String bookId = item.getId();
        String prequelId = item.getPrequel() != null ? item.getPrequel().getId() : null;
        if (bookId != null && prequelId != null && bookId.equals(prequelId)) {
            throw new ItemCannotBeItsOwnPrequelException(
                    "El libro no puede ser su propio predecesor. Libro ID: " + sanitize(bookId));
        }
    }

    private void checkUniqueIsbn(Book item) throws ItemAlreadyExistsException {
        String isbn = item.getIsbn();
        if (isbn != null && repository.existsByIsbn(isbn)) {
            throw new ItemAlreadyExistsException("El ISBN ya existe en otro libro. ISBN: " + sanitize(isbn));
        }
    }

    private void checkSagaFieldsMustBeEmptyIfNotSaga(Book item) throws SagaFieldsMustBeEmptyIfNotSagaException {
        String saga = item.getSaga();
        if (saga == null || saga.isEmpty()) {
            if (item.getNumber() != null) {
                throw new SagaFieldsMustBeEmptyIfNotSagaException(
                        "El número de libro en la saga debe estar vacío si no pertenece a una saga.");
            }
            if (item.getPrequel() != null) {
                throw new SagaFieldsMustBeEmptyIfNotSagaException(
                        "El predecesor debe estar vacío si no pertenece a una saga.");
            }
            if (item.getSequel() != null) {
                throw new SagaFieldsMustBeEmptyIfNotSagaException(
                        "El sucesor debe estar vacío si no pertenece a una saga.");
            }
        }
    }

    private void checkUniqueItemNumberInSaga(Book item) throws DuplicateItemNumberInSagaException {
        String saga = item.getSaga();
        Integer number = item.getNumber();
        if (saga != null && !saga.isEmpty() && number != null) {
            boolean exists = repository.existsBySagaAndNumber(saga, number);
            if (exists) {
                throw new DuplicateItemNumberInSagaException("Ya existe un libro con el mismo número en la saga. Saga: "
                        + sanitize(saga) + ", Número: " + number);
            }
        }
    }

    @Override
    protected Book createEntity() {
        return Book.builder().build();
    }

    @Override
    protected void fillSpecificFields(Book item, BookCreateRequest request) {
        Book prequel = this.findById(request.getPrequelId());
        Book sequel = this.findById(request.getSequelId());

        item.setAuthor(sanitize(request.getAuthor()));
        item.setIsbn(sanitize(request.getIsbn()));
        item.setSaga(sanitize(request.getSaga()));
        item.setNumber(request.getNumber());
        item.setType(request.getType());
        item.setPrequel(prequel);
        item.setSequel(sequel);
    }

    @Override
    protected void postCreationActions(Book item) throws ItemNotFoundException, IllegalArgumentException {
        updatePrequel(item);
        updateSequel(item);
    }

    @Override
    protected void postUpdateActions(Book oldItem, BookCreateRequest request)
            throws ItemNotFoundException, IllegalArgumentException {
        // TODO: Update prequel and sequel relationships if they have changed
    }

    private void updatePrequel(Book item) throws ItemNotFoundException, IllegalArgumentException {
        if (item.getPrequel() != null) {
            Book prequel = this.findById(item.getPrequel().getId());
            prequel.setSequel(item);
            repository.save(prequel);
        }

    }

    private void updateSequel(Book item) throws ItemNotFoundException, IllegalArgumentException {
        if (item.getSequel() != null) {
            Book sequel = this.findById(item.getSequel().getId());
            sequel.setPrequel(item);
            repository.save(sequel);
        }
    }

    @Override
    protected void preDeletionActions(Book item) {
        if (item.getPrequel() != null) {
            Book prequel = this.findById(item.getPrequel().getId());
            prequel.setSequel(null);
            repository.save(prequel);
        }
        if (item.getSequel() != null) {
            Book sequel = this.findById(item.getSequel().getId());
            sequel.setPrequel(null);
            repository.save(sequel);
        }
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

}
