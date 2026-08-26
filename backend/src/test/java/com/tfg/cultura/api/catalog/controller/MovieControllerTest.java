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

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.tfg.cultura.api.catalog.exception.item.ItemAlreadyExistsException;
import com.tfg.cultura.api.catalog.exception.item.ItemNotFoundException;
import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.Movie;
import com.tfg.cultura.api.catalog.model.dto.MovieRequest;
import com.tfg.cultura.api.catalog.model.dto.MovieResponse;
import com.tfg.cultura.api.catalog.service.MovieService;
import com.tfg.cultura.api.core.factory.FileFactory;
import com.tfg.cultura.api.utils.BaseControllerTest;

class MovieControllerTest extends BaseControllerTest {

    @Mock
    private MovieService movieService;

    private static final String BASE_URL = "/api/catalog/movies";
    private static final String MOVIE_URL = BASE_URL + "/{id}";

    private Movie movie;
    private MovieRequest movieCreateRequest;
    private MovieResponse movieResponse;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);
        MovieController controller = new MovieController(movieService);
        mockMvc = buildMockMvc(controller);
        initTestData();
    }

    private void initTestData() {
        movie = CatalogFactory.validMovie();
        movieCreateRequest = CatalogFactory.validMovieCreateRequest();
        movieResponse = new MovieResponse(movie);
    }

    private MockMultipartFile mockMoviePart(MovieRequest request) throws Exception {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        return new MockMultipartFile(
                "item",
                "movie.json",
                MediaType.APPLICATION_JSON_VALUE,
                mapper.writeValueAsString(request).getBytes(StandardCharsets.UTF_8));
    }

    // ====================== CREATION ======================

    @Test
    void should_create_movie_successfully_without_image() throws Exception {
        when(movieService.create(any(MovieRequest.class), isNull())).thenReturn(movieResponse);

        MockMultipartFile moviePart = mockMoviePart(movieCreateRequest);

        mockMvc.perform(multipart(BASE_URL)
                .file(moviePart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(movieResponse.getId()))
                .andExpect(jsonPath("$.name").value(movieResponse.getName()))
                .andExpect(jsonPath("$.format").value(movieResponse.getFormat()));

        verify(movieService).create(any(MovieRequest.class), isNull());
    }

    @Test
    void should_create_movie_successfully_with_image() throws Exception {
        when(movieService.create(any(MovieRequest.class), any())).thenReturn(movieResponse);

        MockMultipartFile moviePart = mockMoviePart(movieCreateRequest);
        MockMultipartFile imagePart = FileFactory.mockImagePart();

        mockMvc.perform(multipart(BASE_URL)
                .file(moviePart)
                .file(imagePart))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(movieResponse.getId()))
                .andExpect(jsonPath("$.name").value(movieResponse.getName()))
                .andExpect(jsonPath("$.format").value(movieResponse.getFormat()));

        verify(movieService).create(any(MovieRequest.class), any());
    }

    @Test
    void should_return_conflict_when_movie_already_exists() throws Exception {
        when(movieService.create(any(MovieRequest.class), any()))
                .thenThrow(new ItemAlreadyExistsException(Map.of("movie", "Ya existe una película con el mismo nombre, año de estreno y formato")));

        MockMultipartFile moviePart = mockMoviePart(movieCreateRequest);

        mockMvc.perform(multipart(BASE_URL)
                .file(moviePart))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors.movie").exists());

        verify(movieService).create(any(MovieRequest.class), any());
    }

    @Test
    void should_return_bad_request_when_create_request_is_invalid() throws Exception {
        movieCreateRequest.setName("");

        MockMultipartFile moviePart = mockMoviePart(movieCreateRequest);

        mockMvc.perform(multipart(BASE_URL)
                .file(moviePart))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(movieService);
    }

    // ====================== GET BY ID ======================

    @Test
    void should_get_movie_by_id() throws Exception {
        when(movieService.getById(anyString())).thenReturn(movieResponse);

        mockMvc.perform(get(MOVIE_URL, movie.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movie.getId()))
                .andExpect(jsonPath("$.name").value(movie.getName()))
                .andExpect(jsonPath("$.format").value(movie.getFormat().name()));

        verify(movieService).getById(movie.getId());
    }

    @Test
    void should_return_404_when_movie_is_not_found() throws Exception {
        when(movieService.getById(anyString()))
                .thenThrow(new ItemNotFoundException("La película no existe"));

        mockMvc.perform(get(MOVIE_URL, "missing-id"))
                .andExpect(status().isNotFound());

        verify(movieService).getById("missing-id");
    }

    // ====================== GET ALL ======================

    @Test
    void should_get_paginated_movies() throws Exception {
        Page<MovieResponse> page = new PageImpl<>(
                List.of(movieResponse),
                PageRequest.of(0, 10),
                1);

        when(movieService.getAll(PageRequest.of(0, 10), null, null)).thenReturn(page);

        mockMvc.perform(get(BASE_URL)
                .param("page", "0")
                .param("size", "10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content.length()").value(1))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(10))
                .andExpect(jsonPath("$.number").value(0));

        verify(movieService).getAll(PageRequest.of(0, 10), null, null);
    }

    // ====================== DELETE ======================

    @Test
    void should_delete_movie_successfully() throws Exception {
        mockMvc.perform(delete(MOVIE_URL, movie.getId()))
                .andExpect(status().isNoContent());

        verify(movieService).delete(movie.getId());
    }

    @Test
    void should_return_404_when_deleting_movie_that_does_not_exist() throws Exception {
        doThrow(new ItemNotFoundException("La película no existe"))
                .when(movieService).delete(anyString());

        mockMvc.perform(delete(MOVIE_URL, "missing-id"))
                .andExpect(status().isNotFound());

        verify(movieService).delete("missing-id");
    }

    // ====================== UPDATE ======================

    @Test
    void should_update_movie_successfully() throws Exception {
        when(movieService.update(anyString(), any(MovieRequest.class), any())).thenReturn(movieResponse);

        MockMultipartFile moviePart = mockMoviePart(movieCreateRequest);

        mockMvc.perform(multipart(MOVIE_URL, movie.getId())
                .file(moviePart)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(movie.getId()))
                .andExpect(jsonPath("$.name").value(movie.getName()));

        verify(movieService).update(anyString(), any(MovieRequest.class), any());
    }

    @Test
    void should_return_conflict_when_updating_movie_that_already_exists() throws Exception {
        when(movieService.update(anyString(), any(MovieRequest.class), any()))
                .thenThrow(new ItemAlreadyExistsException(Map.of("movie", "Ya existe una película con el mismo nombre, año de estreno y formato")));

        MockMultipartFile moviePart = mockMoviePart(movieCreateRequest);

        mockMvc.perform(multipart(MOVIE_URL, movie.getId())
                .file(moviePart)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.errors.movie").exists());

        verify(movieService).update(anyString(), any(MovieRequest.class), any());
    }

    @Test
    void should_return_bad_request_when_update_request_is_invalid() throws Exception {
        movieCreateRequest.setName("");

        MockMultipartFile moviePart = mockMoviePart(movieCreateRequest);

        mockMvc.perform(multipart(MOVIE_URL, movie.getId())
                .file(moviePart)
                .with(request -> {
                    request.setMethod("PUT");
                    return request;
                }))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(movieService);
    }
}
