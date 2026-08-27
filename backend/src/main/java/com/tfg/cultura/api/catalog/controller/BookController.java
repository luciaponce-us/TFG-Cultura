package com.tfg.cultura.api.catalog.controller;

import com.tfg.cultura.api.catalog.model.dto.BookRequest;
import com.tfg.cultura.api.catalog.model.dto.BookResponse;
import com.tfg.cultura.api.catalog.service.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/catalog/books")
@Tag(name = "Catalog - Books", description = "Gestión de libros")
public class BookController extends AbstractItemController<BookRequest, BookResponse, BookService> {

	public BookController(BookService bookService) {
		super(bookService);
	}

}
