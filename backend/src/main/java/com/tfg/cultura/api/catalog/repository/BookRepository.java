package com.tfg.cultura.api.catalog.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.tfg.cultura.api.catalog.model.Book;

public interface BookRepository extends ItemRepository<Book> {
    Page<Book> findAll(Pageable pageable);

    boolean existsByIsbn(String isbn);

    boolean existsBySagaAndNumber(String saga, Integer number);
}
