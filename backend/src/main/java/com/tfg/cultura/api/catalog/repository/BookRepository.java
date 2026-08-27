package com.tfg.cultura.api.catalog.repository;

import com.tfg.cultura.api.catalog.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepository extends AbstractItemRepository<Book> {
	Page<Book> findAll(Pageable pageable);

	boolean existsByIsbn(String isbn);

	Iterable<Book> findAllBySaga(String sagaId);
}
