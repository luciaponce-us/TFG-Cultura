package com.tfg.cultura.api.catalog.service;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDate;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.tfg.cultura.api.catalog.exception.item.ItemAlreadyExistsException;
import com.tfg.cultura.api.catalog.factory.CatalogFactory;
import com.tfg.cultura.api.catalog.model.Movie;
import com.tfg.cultura.api.catalog.model.MovieInfo;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.dto.MovieRequest;
import com.tfg.cultura.api.catalog.model.enumerators.Format;
import com.tfg.cultura.api.catalog.repository.MovieRepository;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.service.SectionService;

@ExtendWith(MockitoExtension.class)
class MovieServiceTest {

    @Mock
    private MovieRepository movieRepository;

    @Mock
    private SectionService sectionService;

    @Mock
    private CategoryService categoryService;

    @Mock
    private FileService fileService;

    @Mock
    private SagaService sagaService;

    @InjectMocks
    private MovieService service;

    @Test
    void should_throw_exception_when_movie_with_same_name_format_and_year_exists() {
        Movie existingMovie = Movie.builder()
                .id("existing-id")
                .name("Test Movie")
                .format(Format.DVD)
                .movieInfo(MovieInfo.builder()
                        .releaseDate(LocalDate.of(2020, 1, 1))
                        .build())
                .build();

        Movie currentMovie = Movie.builder()
                .id("new-id")
                .name("Test Movie")
                .format(Format.DVD)
                .movieInfo(MovieInfo.builder()
                        .releaseDate(LocalDate.of(2020, 1, 1))
                        .build())
                .build();

        when(movieRepository.findByNameAndFormat(currentMovie.getName(), currentMovie.getFormat()))
                .thenReturn(Optional.of(existingMovie));

        assertThrows(ItemAlreadyExistsException.class, () -> service.validate(currentMovie));

        verify(movieRepository).findByNameAndFormat(currentMovie.getName(), currentMovie.getFormat());
    }

    @Test
    void should_not_throw_when_movie_has_different_release_year() {
        Movie existingMovie = Movie.builder()
                .id("existing-id")
                .name("Test Movie")
                .format(Format.DVD)
                .movieInfo(MovieInfo.builder()
                        .releaseDate(LocalDate.of(2019, 1, 1))
                        .build())
                .build();

        Movie currentMovie = Movie.builder()
                .id("new-id")
                .name("Test Movie")
                .format(Format.DVD)
                .movieInfo(MovieInfo.builder()
                        .releaseDate(LocalDate.of(2020, 1, 1))
                        .build())
                .build();

        when(movieRepository.findByNameAndFormat(currentMovie.getName(), currentMovie.getFormat()))
                .thenReturn(Optional.of(existingMovie));

        assertDoesNotThrow(() -> service.validate(currentMovie));
    }

    @Test
    void should_fill_movie_specific_fields() throws Exception {
        MovieRequest request = CatalogFactory.validMovieCreateRequest();
        Saga saga = CatalogFactory.validSaga();

        when(sagaService.findByName(request.getSagaName())).thenReturn(saga);

        Movie movie = new Movie();

        service.fillSpecificFields(movie, request);

        assertEquals(request.getFormat(), movie.getFormat());
        assertEquals(request.getNumberOfDiscs(), movie.getNumberOfDiscs());
        assertEquals(request.getReleaseDate(), movie.getMovieInfo().getReleaseDate());
        assertEquals(request.getTrailerUrl(), movie.getMovieInfo().getTrailerUrl());
        assertEquals(saga, movie.getMovieInfo().getSaga());

        verify(sagaService).findByName(request.getSagaName());
    }

    @Test
    void should_return_3_days_for_single_disc() {
        MovieRequest request = MovieRequest.builder()
                .numberOfDiscs(1)
                .build();

        assertEquals(3, service.getLoanDays(request));
    }

    @Test
    void should_return_7_days_for_multiple_discs() {
        MovieRequest request = MovieRequest.builder()
                .numberOfDiscs(2)
                .build();

        assertEquals(7, service.getLoanDays(request));
    }
}
