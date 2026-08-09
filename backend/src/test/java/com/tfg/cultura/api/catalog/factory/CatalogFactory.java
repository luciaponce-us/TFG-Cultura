package com.tfg.cultura.api.catalog.factory;

import java.time.LocalDate;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.model.Movie;
import com.tfg.cultura.api.catalog.model.MovieInfo;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.dto.BookRequest;
import com.tfg.cultura.api.catalog.model.dto.MovieRequest;
import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.catalog.model.enumerators.Format;

public class CatalogFactory {

    public static Category validCategory() {
        return Category.builder()
                .id("1")
                .name("Test Category")
                .build();
    }

    public static Saga validSaga() {
        return Saga.builder()
                .id("1")
                .name("Test Saga")
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

    public static BookRequest validBookCreateRequest() {
        return BookRequest.builder()
                .name("Test Book")
                .author("Test Author")
                .isbn("1234567890")
                .type(BookType.NOVEL)
                .build();
    }

    public static Movie validMovie() {
        MovieInfo movieInfo = MovieInfo.builder()
                .releaseDate(LocalDate.of(2020, 1, 1))
                .trailerUrl("https://example.com/trailer")
                .saga(validSaga())
                .build();

        return Movie.builder()
                .id("1")
                .name("Test Movie")
                .format(Format.DVD)
                .numberOfDiscs(1)
                .movieInfo(movieInfo)
                .build();
    }

    public static MovieRequest validMovieCreateRequest() {
        return MovieRequest.builder()
                .name("Test Movie")
                .format(Format.DVD)
                .numberOfDiscs(1)
                .releaseDate(LocalDate.of(2020, 1, 1))
                .trailerUrl("https://example.com/trailer")
                .sagaName(validSaga().getName())
                .build();
    }

    public static MockMultipartFile mockImagePart() {
        return new MockMultipartFile(
                "image",
                "image.jpg",
                MediaType.IMAGE_JPEG_VALUE,
                "dummy image content".getBytes());
    }

}