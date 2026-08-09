package com.tfg.cultura.api.catalog.service;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.tfg.cultura.api.catalog.exception.item.ItemAlreadyExistsException;
import com.tfg.cultura.api.catalog.exception.saga.SagaNotFoundException;
import com.tfg.cultura.api.catalog.model.Movie;
import com.tfg.cultura.api.catalog.model.MovieInfo;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.dto.MovieRequest;
import com.tfg.cultura.api.catalog.model.dto.MovieResponse;
import com.tfg.cultura.api.catalog.repository.MovieRepository;
import com.tfg.cultura.api.core.service.FileService;
import com.tfg.cultura.api.sections.service.SectionService;

@Service
public class MovieService extends AbstractItemService<Movie, MovieRepository, MovieRequest, MovieResponse> {

    private final SagaService sagaService;

    public MovieService(MovieRepository movieRepository, SectionService sectionService, CategoryService categoryService,
            FileService fileService, SagaService sagaService) {
        super(movieRepository, sectionService, categoryService, fileService, MovieResponse::new);
        this.sagaService = sagaService;
    }

    @Override
    protected String getImageFolder() {
        return "cultura/items/movies";
    }

    @Override
    protected String getDefaultImageUrl() {
        return "https://res.cloudinary.com/dubz79y98/image/upload/v1786126736/movie_placeholder.jpg";
    }

    @Override
    protected void validate(Movie item) {
        checkUniqueNameReleaseYearAndFormat(item);
    }

    private void checkUniqueNameReleaseYearAndFormat(Movie item) throws ItemAlreadyExistsException {
        Optional<Movie> existingMovie = repository.findByNameAndFormat(item.getName(), item.getFormat());
        
        if (existingMovie.isPresent() && !existingMovie.get().getId().equals(item.getId())) {
            LocalDate existingReleaseDate = existingMovie.get().getMovieInfo().getReleaseDate();
            boolean sameReleaseYear = existingReleaseDate.getYear() == item.getMovieInfo().getReleaseDate().getYear();
            if (sameReleaseYear) {
                throw new ItemAlreadyExistsException("Ya existe una película con el mismo nombre, año de estreno y formato");
            }
        }
    }

    @Override
    protected Movie createEntity() {
        return Movie.builder().build();
    }

    @Override
    protected void fillSpecificFields(Movie item, MovieRequest request) throws SagaNotFoundException {
        Saga saga = sagaService.findByName(request.getSagaName());

        MovieInfo movieInfo = MovieInfo.builder()
                .releaseDate(request.getReleaseDate())
                .trailerUrl(request.getTrailerUrl())
                .saga(saga)
                .build();

        item.setFormat(request.getFormat());
        item.setNumberOfDiscs(request.getNumberOfDiscs());
        item.setMovieInfo(movieInfo);
    }

    @Override
    protected Integer getLoanDays(MovieRequest request) {
        switch(request.getNumberOfDiscs()){
            case 1:
                return 3;
            default:
                return 7;
        }
    }
    
}
