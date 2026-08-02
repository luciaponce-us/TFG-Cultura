package com.tfg.cultura.api.catalog.service;

import java.util.Optional;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tfg.cultura.api.catalog.exception.BookNotFoundException;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.model.dto.BookCreateRequest;
import com.tfg.cultura.api.catalog.model.dto.BookResponse;
import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.catalog.repository.BookRepository;
import com.tfg.cultura.api.core.exception.FileUploadException;
import com.tfg.cultura.api.core.model.dto.FileUploadRequest;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.core.utils.LoggerSanitizer;
import com.tfg.cultura.api.sections.model.Section;
import com.tfg.cultura.api.sections.service.SectionService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookService {
    private final BookRepository bookRepository;

    private final SectionService sectionService;
    private final CategoryService categoryService;
    private final FileService fileService;

    private static final Logger logger = LoggerFactory.getLogger("catalogLogger");
    private static final Integer MAX_IMAGE_SIZE_MB = 2;
    private static final String BOOK_IMAGE_FOLDER = "cultura/items/books";
    private static final String DEFAULT_IMAGE_URL = "https://res.cloudinary.com/dubz79y98/image/upload/v1785682202/book_placeholder.jpg";


    // HELPER

    void checkAvailableCopies(Integer availableCopies, Integer copies) throws IllegalArgumentException {
        if (availableCopies < 0) {
            logger.error("El número de copias disponibles no puede ser menor que 0");
            throw new IllegalArgumentException("El número de copias disponibles no puede ser menor que 0");
        }
        if (availableCopies > copies) {
            logger.error("El número de copias disponibles no puede ser mayor que el número total de copias");
            throw new IllegalArgumentException(
                    "El número de copias disponibles no puede ser mayor que el número total de copias");
        }
    }

    String uploadBookImage(String bookId, MultipartFile file) throws FileUploadException {
        if (file != null && !file.isEmpty()) {
            String id = LoggerSanitizer.sanitize(bookId);

            fileService.validateImageSize(file, MAX_IMAGE_SIZE_MB);
            MultipartFile resizedImage = fileService.resizeImage(file, 400, 600);

            FileUploadRequest request = FileUploadRequest.builder()
                    .file(resizedImage)
                    .folder(BOOK_IMAGE_FOLDER)
                    .publicId("book_" + id)
                    .resourceType("image")
                    .build();
            String imageUrl = fileService.uploadFile(request);
            logger.info("Se ha subido la imagen {} para el libro con id {}", imageUrl, id);

            return imageUrl;
        }
        return DEFAULT_IMAGE_URL;
    }

    Integer getLoanDaysByBookType(BookType type) {
        switch (type) {
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

    private String sanitizeString(String input) {
        return LoggerSanitizer.sanitize(input);
    }

    // GET

    public Book getBookById(String id) throws BookNotFoundException {
        Optional<Book> book = bookRepository.findById(id);
        if (book.isEmpty()) {
            logger.error("Libro no encontrado con ID: {}", id);
            throw new BookNotFoundException("Libro no encontrado con ID: " + id);
        }
        return book.get();
    }

    // CREATE

    public BookResponse createBook(BookCreateRequest request, MultipartFile image) throws BookNotFoundException {
        checkAvailableCopies(request.getAvailableCopies(), request.getCopies());
        Section section = sectionService.findSectionById(request.getSectionId());
        Set<Category> categories = categoryService.findCategoriesByIds(request.getCategoriesIds());
        Book prequel = getBookById(request.getPrequelId());
        Book sequel = getBookById(request.getSequelId());

        Book book = Book.builder()
                // Item fields
                .name(sanitizeString(request.getName()))
                .description(sanitizeString(request.getDescription()))
                .condition(request.getCondition())
                .comments(sanitizeString(request.getComments()))
                .loanAvailable(request.getLoanAvailable())
                .publicated(request.getPublicated())
                .purchasedAt(request.getPurchasedAt())
                .price(request.getPrice())
                .copies(request.getCopies())
                .availableCopies(request.getAvailableCopies())
                .loanDays(getLoanDaysByBookType(request.getType()))
                .section(section)
                .categories(categories)
                // Book fields
                .author(sanitizeString(request.getAuthor()))
                .isbn(sanitizeString(request.getIsbn()))
                .saga(sanitizeString(request.getSaga()))
                .number(request.getNumber())
                .type(request.getType())
                .prequel(prequel)
                .sequel(sequel)
                .build();

        String imageUrl = uploadBookImage(book.getId(), image);
        book.setImageUrl(imageUrl);

        Book savedBook = bookRepository.save(book);

        return new BookResponse(savedBook);
    }

}
