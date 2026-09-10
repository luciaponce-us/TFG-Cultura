package com.tfg.cultura.api.catalog.controller;

import com.tfg.cultura.api.catalog.model.dto.BookRequest;
import com.tfg.cultura.api.catalog.model.dto.BookResponse;
import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.catalog.service.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;

import java.util.Set;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog/books")
@Tag(name = "Catalog - Books", description = "Gestión de libros")
public class BookController extends AbstractItemController<BookRequest, BookResponse, BookService> {

	public BookController(BookService bookService) {
		super(bookService);
	}

	@GetMapping("/types/{types}")
	public ResponseEntity<Page<BookResponse>> getAllBooksByType(@PathVariable Set<BookType> types,
			@RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "10") int size,
			@RequestParam(required = false) String nameContains,
			@RequestParam(required = false) Set<String> categoryIds) {
		Page<BookResponse> response = service.getAllBooksByTypeAndNameContains(types, nameContains, categoryIds,
				PageRequest.of(page, size));
		return ResponseEntity.status(HttpStatus.OK).body(response);
	}

}
