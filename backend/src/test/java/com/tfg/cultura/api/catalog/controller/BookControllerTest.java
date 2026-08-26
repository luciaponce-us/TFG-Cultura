package com.tfg.cultura.api.catalog.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.tfg.cultura.api.catalog.exception.item.ItemAlreadyExistsException;
import com.tfg.cultura.api.catalog.exception.item.ItemNotFoundException;
import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.dto.BookRequest;
import com.tfg.cultura.api.catalog.model.dto.BookResponse;
import com.tfg.cultura.api.catalog.service.BookService;
import com.tfg.cultura.api.core.factory.FileFactory;
import com.tfg.cultura.api.utils.BaseControllerTest;

class BookControllerTest extends BaseControllerTest {

    @Mock
    private BookService bookService;

    private static final String BASE_URL = "/api/catalog/books";
    private static final String BOOK_URL = BASE_URL + "/{id}";

    private Book book;
    private BookRequest bookCreateRequest;
    private BookResponse bookResponse;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        BookController controller = new BookController(bookService);
        mockMvc = buildMockMvc(controller);
        initTestData();
    }

    private void initTestData() {
        book = CatalogFactory.validBook();
        bookCreateRequest = CatalogFactory.validBookCreateRequest();
        bookResponse = new BookResponse(book);
    }

    private MockMultipartFile mockBookPart(BookRequest request) throws Exception {
        return new MockMultipartFile(
                "item",
                "book.json",
                MediaType.APPLICATION_JSON_VALUE,
                toJson(request).getBytes(StandardCharsets.UTF_8));
    }

    // ====================== CREATION ======================

    @Test
    void should_create_book_successfully_without_image() throws Exception {
        when(bookService.create(any(BookRequest.class), isNull())).thenReturn(bookResponse);

        MockMultipartFile bookPart = mockBookPart(bookCreateRequest);

        mockMvc.perform(multipart(BASE_URL)
                .file(bookPart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookResponse.getId()))
                .andExpect(jsonPath("$.name").value(bookResponse.getName()))
                .andExpect(jsonPath("$.author").value(bookResponse.getAuthor()));

        verify(bookService).create(any(BookRequest.class), isNull());
    }

    @Test
    void should_create_book_successfully_with_image() throws Exception {
        when(bookService.create(any(BookRequest.class), any())).thenReturn(bookResponse);

        MockMultipartFile bookPart = mockBookPart(bookCreateRequest);
        MockMultipartFile imagePart = FileFactory.mockImagePart();

        mockMvc.perform(multipart(BASE_URL)
                .file(bookPart)
                .file(imagePart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(bookResponse.getId()))
                .andExpect(jsonPath("$.author").value(bookResponse.getAuthor()));

        verify(bookService).create(any(BookRequest.class), any());
    }

    @Test
    void should_return_conflict_when_book_already_exists() throws Exception {
        when(bookService.create(any(BookRequest.class), any()))
                .thenThrow(new ItemAlreadyExistsException(Map.of("isbn", "El ISBN ya existe")));

        MockMultipartFile bookPart = mockBookPart(bookCreateRequest);

        mockMvc.perform(multipart(BASE_URL)
                .file(bookPart))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors.isbn").value("El ISBN ya existe"));

        verify(bookService).create(any(BookRequest.class), any());
    }

    @Test
    void should_return_bad_request_when_create_request_is_invalid() throws Exception {
        bookCreateRequest.setName("");

        MockMultipartFile bookPart = mockBookPart(bookCreateRequest);

        mockMvc.perform(multipart(BASE_URL)
                .file(bookPart))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookService);
    }

    // ====================== GET BY ID ======================

    @Test
    void should_get_book_by_id() throws Exception {
        when(bookService.getById(anyString())).thenReturn(bookResponse);

        mockMvc.perform(get(BOOK_URL, book.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.name").value(book.getName()))
                .andExpect(jsonPath("$.author").value(book.getAuthor()));

        verify(bookService).getById(book.getId());
    }

    @Test
    void should_return_404_when_book_is_not_found() throws Exception {
        when(bookService.getById(anyString()))
                .thenThrow(new ItemNotFoundException("El libro no existe"));

        mockMvc.perform(get(BOOK_URL, "missing-id"))
                .andExpect(status().isNotFound());

        verify(bookService).getById("missing-id");
    }

    // ====================== GET ALL ======================

    @Test
    void should_get_paginated_books() throws Exception {
        Page<BookResponse> page = new PageImpl<>(
                List.of(bookResponse),
                PageRequest.of(0, 10),
                1);

        when(bookService.getAll(PageRequest.of(0, 10), null, null)).thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));

        verify(bookService).getAll(PageRequest.of(0, 10), null, null);
    }

    // ====================== DELETE ======================

    @Test
    void should_delete_book_successfully() throws Exception {
        mockMvc.perform(delete(BOOK_URL, book.getId()))
                .andExpect(status().isNoContent());

        verify(bookService).delete(book.getId());
    }

    @Test
    void should_return_404_when_deleting_book_that_does_not_exist() throws Exception {
        doThrow(new ItemNotFoundException("El libro no existe"))
                .when(bookService).delete(anyString());

        mockMvc.perform(delete(BOOK_URL, "missing-id"))
                .andExpect(status().isNotFound());

        verify(bookService).delete("missing-id");
    }

    // ====================== UPDATE ======================

    @Test
    void should_update_book_successfully() throws Exception {
        when(bookService.update(anyString(), any(BookRequest.class), any())).thenReturn(bookResponse);

        MockMultipartFile bookPart = mockBookPart(bookCreateRequest);

        mockMvc.perform(multipart(BOOK_URL, book.getId())
                .file(bookPart)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(book.getId()))
                .andExpect(jsonPath("$.name").value(book.getName()));

        verify(bookService).update(anyString(), any(BookRequest.class), any());
    }

    @Test
    void should_return_bad_request_when_update_request_is_invalid() throws Exception {
        bookCreateRequest.setName("");

        MockMultipartFile bookPart = mockBookPart(bookCreateRequest);

        mockMvc.perform(multipart(BOOK_URL, book.getId())
                .file(bookPart)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(bookService);
    }
}
