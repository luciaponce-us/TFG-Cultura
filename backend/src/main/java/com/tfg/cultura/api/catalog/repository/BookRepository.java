package com.tfg.cultura.api.catalog.repository;

import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.categories.model.Category;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BookRepository extends AbstractItemRepository<Book> {
	Page<Book> findAll(Pageable pageable);
	Page<Book> findAllByTypeIn(Set<BookType> types, Pageable pageable);
	Page<Book> findAllByTypeInAndNameContainingIgnoreCase(Set<BookType> types, String name, Pageable pageable);
	Page<Book> findAllByTypeInAndCategoriesContaining(Set<BookType> types, Set<Category> categories, Pageable pageable);
	Page<Book> findAllByTypeInAndNameContainingIgnoreCaseAndCategoriesContaining(Set<BookType> types, String name,
			Set<Category> categories, Pageable pageable);

	boolean existsByIsbn(String isbn);

	Iterable<Book> findAllBySaga(String sagaId);
}
