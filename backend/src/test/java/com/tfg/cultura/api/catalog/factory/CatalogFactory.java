package com.tfg.cultura.api.catalog.factory;

import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.model.dto.BookCreateRequest;
import com.tfg.cultura.api.catalog.model.enumerators.BookType;

public class CatalogFactory {

    public static Category validCategory() {
        return Category.builder()
                .id("1")
                .name("Test Category")
                .build();
    }

    public static Book validBook() {
        return Book.builder()
                .id("1")
                .name("Test Book")
                .author("Test Author")
                .isbn("1234567890")
                .build();
    }

    public static BookCreateRequest validBookCreateRequest() {
        return BookCreateRequest.builder()
                .name("Test Book")
                .author("Test Author")
                .isbn("1234567890")
                .type(BookType.NOVEL)
                .build();
    }

}