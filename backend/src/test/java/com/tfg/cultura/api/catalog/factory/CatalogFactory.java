package com.tfg.cultura.api.catalog.factory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;

import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.model.Movie;
import com.tfg.cultura.api.catalog.model.MovieInfo;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.Season;
import com.tfg.cultura.api.catalog.model.Series;
import com.tfg.cultura.api.catalog.model.SeriesInfo;
import com.tfg.cultura.api.catalog.model.dto.BookRequest;
import com.tfg.cultura.api.catalog.model.dto.MovieRequest;
import com.tfg.cultura.api.catalog.model.dto.SeriesRequest;
import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.catalog.model.enumerators.Format;
import com.tfg.cultura.api.catalog.model.enumerators.SeriesStatus;
import com.tfg.cultura.api.sections.factory.SectionFactory;

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

    public static Season validSeason() {
        return Season.builder()
                .seasonNumber(1)
                .seasonPart(1)
                .trailerUrl("https://example.com/season-trailer")
                .build();
    }

    public static Series validSeries() {

        SeriesInfo info = SeriesInfo.builder()
                .releaseDate(LocalDate.of(2011, 04, 17))
                .numberOfSeasons(8)
                .status(SeriesStatus.FINISHED)
                .build();

        Season season = validSeason();

        return Series.builder()
                .name("Juego de Tronos")
                .purchasedAt(LocalDate.of(2015, 01, 01)) // Optional
                .price(BigDecimal.valueOf(15.99)) // Optional
                .loanDays(14) // Optional
                .section(SectionFactory.validSection()) // Optional
                .categories(Set.of(validCategory())) // Optional
                // Series fields
                .format(Format.DVD)
                .numberOfDiscs(2)
                .seriesInfo(info)
                .seasons(new ArrayList<Season>(List.of(season)))
                .build();
    }

    public static SeriesRequest validSeriesRequest() {
        Series series = validSeries();
        Set<String> categoriesIds = series.getCategories().stream().map(c -> c.getId()).collect(Collectors.toSet());

        return SeriesRequest.builder()
                .name(series.getName())
                .purchasedAt(series.getPurchasedAt()) // Optional
                .price(series.getPrice()) // Optional
                .sectionId(series.getSection().getId()) // Optional
                .categoriesIds(categoriesIds) // Optional
                // Series fields
                .format(series.getFormat())
                .numberOfDiscs(series.getNumberOfDiscs())
                .releaseDate(series.getSeriesInfo().getReleaseDate())
                .numberOfSeasons(series.getSeriesInfo().getNumberOfSeasons())
                .status(series.getSeriesInfo().getStatus())
                .seasons(series.getSeasons())
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