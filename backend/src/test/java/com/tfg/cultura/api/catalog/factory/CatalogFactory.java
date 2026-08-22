package com.tfg.cultura.api.catalog.factory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.tfg.cultura.api.catalog.model.BoardGame;
import com.tfg.cultura.api.catalog.model.Book;
import com.tfg.cultura.api.catalog.model.Category;
import com.tfg.cultura.api.catalog.model.Movie;
import com.tfg.cultura.api.catalog.model.MovieInfo;
import com.tfg.cultura.api.catalog.model.RolGame;
import com.tfg.cultura.api.catalog.model.RolSaga;
import com.tfg.cultura.api.catalog.model.Saga;
import com.tfg.cultura.api.catalog.model.Season;
import com.tfg.cultura.api.catalog.model.Series;
import com.tfg.cultura.api.catalog.model.SeriesInfo;
import com.tfg.cultura.api.catalog.model.dto.BoardGameRequest;
import com.tfg.cultura.api.catalog.model.dto.BookRequest;
import com.tfg.cultura.api.catalog.model.dto.MovieRequest;
import com.tfg.cultura.api.catalog.model.dto.RolGameRequest;
import com.tfg.cultura.api.catalog.model.dto.RolSagaRequest;
import com.tfg.cultura.api.catalog.model.dto.SeriesRequest;
import com.tfg.cultura.api.catalog.model.enumerators.BoardGameType;
import com.tfg.cultura.api.catalog.model.enumerators.BookType;
import com.tfg.cultura.api.catalog.model.enumerators.Complexity;
import com.tfg.cultura.api.catalog.model.enumerators.Format;
import com.tfg.cultura.api.catalog.model.enumerators.GameMaster;
import com.tfg.cultura.api.catalog.model.enumerators.RolBookType;
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
                .isbn("9780306406157")
                .build();
    }

    public static BookRequest validBookCreateRequest() {
        return BookRequest.builder()
                .name("Test Book")
                .author("Test Author")
                .isbn("9780306406157")
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

    public static BoardGame validBaseBoardGame() {
        return BoardGame.builder()
                .id("basegame")
                .name("Base Game")
                .minPlayers(2)
                .maxPlayers(4)
                .playTime(60)
                .complexity(Complexity.MEDIUM)
                .types(Set.of(BoardGameType.STRATEGY))
                .build();
    }

    public static BoardGame validBoardGame() {
        BoardGame baseGame = validBaseBoardGame();

        return BoardGame.builder()
                .id("boardgame")
                .name("Test Board Game")
                .purchasedAt(LocalDate.of(2015, 01, 01)) // Optional
                .price(BigDecimal.valueOf(15.99)) // Optional
                .loanDays(14) // Optional
                .section(SectionFactory.validSection()) // Optional
                .categories(Set.of(validCategory())) // Optional
                // BoardGame fields
                .minPlayers(2)
                .maxPlayers(4)
                .playTime(60)
                .complexity(Complexity.MEDIUM)
                .types(Set.of(BoardGameType.STRATEGY))
                .baseGame(baseGame)
                .build();
    }

    public static BoardGameRequest validBoardGameRequest() {
        BoardGame boardGame = validBoardGame();
        Set<String> categoriesIds = boardGame.getCategories().stream().map(c -> c.getId()).collect(Collectors.toSet());
        BoardGameType[] typesArray = boardGame.getTypes().toArray(new BoardGameType[0]);

        return BoardGameRequest.builder()
                .name(boardGame.getName())
                .purchasedAt(boardGame.getPurchasedAt()) // Optional
                .price(boardGame.getPrice()) // Optional
                .sectionId(boardGame.getSection().getId()) // Optional
                .categoriesIds(categoriesIds) // Optional
                // BoardGame fields
                .minPlayers(boardGame.getMinPlayers())
                .maxPlayers(boardGame.getMaxPlayers())
                .playTime(boardGame.getPlayTime())
                .complexity(boardGame.getComplexity())
                .types(typesArray)
                .baseGameId(boardGame.getBaseGame().getId())
                .build();
    }

    // RolSaga

    public static RolSaga validRolSaga() {
        return RolSaga.builder()
                .id("rol-saga-id")
                .name("Test Rol Saga")
                .description("Test Rol Saga valid description")
                .gameMaster(GameMaster.COMPULSORY)
                .section(SectionFactory.validSection())
                .imageUrl("old-image-url")
                .build();
    }

    public static RolSagaRequest validRolSagaRequest() {
        RolSaga rolSaga = validRolSaga();

        return RolSagaRequest.builder()
                .name("Changed Rol Saga Name")
                .description(rolSaga.getDescription())
                .gameMaster(rolSaga.getGameMaster())
                .sectionId(rolSaga.getSection().getId())
                .build();
    }

    // RolGame

    public static RolGame validRolGame() {
        return RolGame.builder()
                .id("rol-game-id")
                .name("Test Rol Game")
                .description("Test Rol Game valid description")
                .type(RolBookType.BASIC)
                .saga(validRolSaga())
                .section(SectionFactory.validSection())
                .imageUrl("old-image-url")
                .saga(validRolSaga())
                .build();
    }

    public static RolGameRequest validRolGameRequest() {
        RolGame rolGame = validRolGame();

        return RolGameRequest.builder()
                .name("Changed Rol Game Name")
                .description(rolGame.getDescription())
                .type(rolGame.getType())
                .sagaId(rolGame.getSaga().getId())
                .sectionId(rolGame.getSection().getId())
                .build();
    }

}