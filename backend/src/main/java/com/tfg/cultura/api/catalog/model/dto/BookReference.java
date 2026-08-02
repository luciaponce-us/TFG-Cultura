package com.tfg.cultura.api.catalog.model.dto;

import com.tfg.cultura.api.catalog.model.Book;

import lombok.Getter;

@Getter
public class BookReference {
    private String id;
    private String name;
    private String imageUrl;
    private Boolean loanAvailable;
    private Boolean publicated;
    private String author;
    private String saga;
    private Integer number;

    public BookReference(Book book) {
        this.id = book.getId();
        this.name = book.getName();
        this.imageUrl = book.getImageUrl();
        this.loanAvailable = book.getLoanAvailable();
        this.publicated = book.getPublicated();
        this.author = book.getAuthor();
        this.saga = book.getSaga();
        this.number = book.getNumber();
    }
    
}
