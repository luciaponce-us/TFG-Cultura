package com.tfg.cultura.api.catalog.factory;

import com.tfg.cultura.api.catalog.model.*;
import com.tfg.cultura.api.catalog.model.dto.*;
import com.tfg.cultura.api.catalog.model.enumerators.*;
import com.tfg.cultura.api.categories.factory.CategoryFactory;
import com.tfg.cultura.api.categories.model.Category;
import com.tfg.cultura.api.sections.factory.SectionFactory;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CatalogFactory {

	private static final String YT_EMBED_URL = "https://www.youtube.com/embed/nsNFOn_4i4E";

	private static final Category validCategory = CategoryFactory.validCategory();

	public static Saga validSaga() {
		return Saga.builder().id("1").name("Test Saga").build();
	}

	public static Book validBook() {
		return Book.builder().id("1").name("Test Book").author("Test Author").isbn("9780306406157").build();
	}

	public static BookRequest validBookCreateRequest() {
		return BookRequest.builder().name("Test Book").author("Test Author").isbn("9780306406157").type(BookType.NOVEL)
				.build();
	}

	public static Movie validMovie() {
		MovieInfo movieInfo = MovieInfo.builder().releaseDate(LocalDate.of(2020, 1, 1)).trailerUrl(YT_EMBED_URL)
				.saga(validSaga()).build();

		return Movie.builder().id("1").name("Test Movie").format(Format.DVD).numberOfDiscs(1).movieInfo(movieInfo)
				.build();
	}

	public static MovieRequest validMovieCreateRequest() {
		return MovieRequest.builder().name("Test Movie").format(Format.DVD).numberOfDiscs(1)
				.releaseDate(LocalDate.of(2020, 1, 1)).trailerUrl(YT_EMBED_URL).sagaName(validSaga().getName()).build();
	}

	public static Season validSeason() {
		return Season.builder().seasonNumber(1).seasonPart(1).trailerUrl(YT_EMBED_URL).build();
	}

	public static Series validSeries() {

		SeriesInfo info = SeriesInfo.builder().releaseDate(LocalDate.of(2011, 04, 17)).numberOfSeasons(8)
				.status(SeriesStatus.FINISHED).build();

		Season season = validSeason();

		return Series.builder().name("Juego de Tronos").purchasedAt(LocalDate.of(2015, 01, 01)) // Optional
				.price(BigDecimal.valueOf(15.99)) // Optional
				.loanDays(14) // Optional
				.section(SectionFactory.validSection()) // Optional
				.categories(Set.of(validCategory)) // Optional
				// Series fields
				.format(Format.DVD).numberOfDiscs(2).seriesInfo(info).seasons(new ArrayList<Season>(List.of(season)))
				.build();
	}

	public static SeriesRequest validSeriesRequest() {
		Series series = validSeries();
		Set<String> categoriesIds = series.getCategories().stream().map(c -> c.getId()).collect(Collectors.toSet());

		return SeriesRequest.builder().name(series.getName()).purchasedAt(series.getPurchasedAt()) // Optional
				.price(series.getPrice()) // Optional
				.sectionId(series.getSection().getId()) // Optional
				.categoriesIds(categoriesIds) // Optional
				// Series fields
				.format(series.getFormat()).numberOfDiscs(series.getNumberOfDiscs())
				.releaseDate(series.getSeriesInfo().getReleaseDate())
				.numberOfSeasons(series.getSeriesInfo().getNumberOfSeasons()).status(series.getSeriesInfo().getStatus())
				.seasons(series.getSeasons()).build();

	}

	public static BoardGame validBaseBoardGame() {
		return BoardGame.builder().id("basegame").name("Base Game").minPlayers(2).maxPlayers(4).playTime(60)
				.complexity(Complexity.MEDIUM).types(Set.of(BoardGameType.STRATEGY)).build();
	}

	public static BoardGame validBoardGame() {
		BoardGame baseGame = validBaseBoardGame();

		return BoardGame.builder().id("boardgame").name("Test Board Game").purchasedAt(LocalDate.of(2015, 01, 01)) // Optional
				.price(BigDecimal.valueOf(15.99)) // Optional
				.loanDays(14) // Optional
				.section(SectionFactory.validSection()) // Optional
				.categories(Set.of(validCategory)) // Optional
				// BoardGame fields
				.minPlayers(2).maxPlayers(4).playTime(60).complexity(Complexity.MEDIUM)
				.types(Set.of(BoardGameType.STRATEGY)).baseGame(baseGame).build();
	}

	public static BoardGameRequest validBoardGameRequest() {
		BoardGame boardGame = validBoardGame();
		Set<String> categoriesIds = boardGame.getCategories().stream().map(c -> c.getId()).collect(Collectors.toSet());
		BoardGameType[] typesArray = boardGame.getTypes().toArray(new BoardGameType[0]);

		return BoardGameRequest.builder().name(boardGame.getName()).purchasedAt(boardGame.getPurchasedAt()) // Optional
				.price(boardGame.getPrice()) // Optional
				.sectionId(boardGame.getSection().getId()) // Optional
				.categoriesIds(categoriesIds) // Optional
				// BoardGame fields
				.minPlayers(boardGame.getMinPlayers()).maxPlayers(boardGame.getMaxPlayers())
				.playTime(boardGame.getPlayTime()).complexity(boardGame.getComplexity()).types(typesArray)
				.baseGameId(boardGame.getBaseGame().getId()).build();
	}

	// RolSaga

	public static RolSaga validRolSaga() {
		return RolSaga.builder().id("rol-saga-id").name("Test Rol Saga").description("Test Rol Saga valid description")
				.gameMaster(GameMaster.COMPULSORY).section(SectionFactory.validSection()).imageUrl("old-image-url")
				.build();
	}

	public static RolSagaRequest validRolSagaRequest() {
		RolSaga rolSaga = validRolSaga();

		return RolSagaRequest.builder().name("Changed Rol Saga Name").description(rolSaga.getDescription())
				.gameMaster(rolSaga.getGameMaster()).sectionId(rolSaga.getSection().getId()).build();
	}

	// RolGame

	public static RolGame validRolGame() {
		return RolGame.builder().id("rol-game-id").name("Test Rol Game").description("Test Rol Game valid description")
				.type(RolBookType.BASIC).saga(validRolSaga()).section(SectionFactory.validSection())
				.imageUrl("old-image-url").saga(validRolSaga()).build();
	}

	public static RolGameRequest validRolGameRequest() {
		RolGame rolGame = validRolGame();

		return RolGameRequest.builder().name("Changed Rol Game Name").description(rolGame.getDescription())
				.type(rolGame.getType()).sagaId(rolGame.getSaga().getId()).sectionId(rolGame.getSection().getId())
				.build();
	}

	// VideoGame

	public static VideoGame validVideoGame() {
		return VideoGame.builder().id("videogame").name("Test Video Game").purchasedAt(LocalDate.of(2020, 06, 01)) // Optional
				.price(BigDecimal.valueOf(15.99)) // Optional
				.loanDays(0) // Optional
				.section(SectionFactory.validSection()) // Optional
				.categories(Set.of(validCategory)) // Optional
				.imageUrl("old-image-url").platform(Platform.PC).releaseDate(LocalDate.of(2020, 01, 01))
				.trailerUrl(YT_EMBED_URL).build();
	}

	public static VideoGameRequest validVideoGameRequest() {
		VideoGame videoGame = validVideoGame();
		Set<String> categoriesIds = videoGame.getCategories().stream().map(c -> c.getId()).collect(Collectors.toSet());

		return VideoGameRequest.builder().name(videoGame.getName()).purchasedAt(videoGame.getPurchasedAt()) // Optional
				.price(videoGame.getPrice()) // Optional
				.sectionId(videoGame.getSection().getId()) // Optional
				.categoriesIds(categoriesIds) // Optional
				.platform(videoGame.getPlatform()).releaseDate(videoGame.getReleaseDate())
				.trailerUrl(videoGame.getTrailerUrl()).build();
	}

}