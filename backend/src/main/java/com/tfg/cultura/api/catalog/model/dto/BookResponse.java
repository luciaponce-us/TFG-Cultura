package com.tfg.cultura.api.catalog.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Set;

import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.catalog.model.enumerators.ItemCondition;
import com.tfg.cultura.api.sections.model.dto.SectionReference;

import lombok.Getter;

@Getter
public class BookResponse {
    // Item fields
    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private ItemCondition condition;
    private String comments;
    private Boolean loanAvailable;
    private Boolean publicated;
    private LocalDate purchasedAt;
    private BigDecimal price;
    private Integer copies;
    private Integer availableCopies;
    private Integer loanDays;
    private SectionReference section;
    private Set<Category> categories;
    private LocalDateTime createdAt;
    // Book-specific fields
    private String author;
    private String isbn;
    private BookType type;
    private String saga;

    public BookResponse(Book book) {
        this.id = book.getId();
        this.name = book.getName();
        this.description = book.getDescription();
        this.imageUrl = book.getImageUrl();
        this.condition = book.getCondition();
        this.comments = book.getComments();
        this.loanAvailable = book.getLoanAvailable();
        this.publicated = book.getPublicated();
        this.purchasedAt = book.getPurchasedAt();
        this.price = book.getPrice();
        this.copies = book.getCopies();
        this.availableCopies = book.getAvailableCopies();
        this.loanDays = book.getLoanDays();
        this.section = book.getSection() != null ? new SectionReference(book.getSection()) : null;
        this.categories = book.getCategories();
        this.createdAt = book.getCreatedAt();

        // Book-specific fields
        this.author = book.getAuthor();
        this.isbn = book.getIsbn();
        this.type = book.getType();
        this.saga = book.getSaga() != null ? book.getSaga().getName() : null;
    }
}
