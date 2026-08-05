package com.tfg.cultura.api.catalog.model.dto;

import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.enumerators.BookType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class BookResponse extends ItemResponse {

    private String author;
    private String isbn;
    private BookType type;
    private String saga;

    public BookResponse(Book book) {
        super(book);

        this.author = book.getAuthor();
        this.isbn = book.getIsbn();
        this.type = book.getType();
        this.saga = book.getSaga() != null
                ? book.getSaga().getName()
                : null;
    }
}
