import {
  ITEM_TYPES,
  type Item,
  type ItemCondition,
  type ItemRequest,
  type ItemType,
} from "../types";
import type { BoardGame, BoardGameRequest } from "../types/boardgame";
import type { Book, BookRequest } from "../types/book";
import type { Movie, MovieRequest } from "../types/movie";
import type {
  RolGame,
  RolGameRequest,
  RolSaga,
  RolSagaRequest,
} from "../types/rolgame";
import type { Format } from "../types/movie";
import type { Series, SeriesRequest } from "../types/series";
import type { VideoGame, VideoGameRequest } from "../types/videogame";
import { PLACEHOLDER } from "@/modules/core/utils/utils";

function toFormatValue(format: string): Format {
  switch (format) {
    case "Blu-ray":
      return "BLURAY";
    case "4K":
      return "UHD_4K";
    default:
      return format as Format;
  }
}

function toRequest(item: Item): ItemRequest {
  return {
    name: item.name,
    description: item.description,
    condition: item.condition,
    comments: item.comments,
    loanAvailable: item.loanAvailable,
    publicated: item.publicated,
    purchasedAt: item.purchasedAt,
    price: item.price,
    copies: item.copies,
    availableCopies: item.availableCopies,
    sectionId: item.section.id,
    categoriesIds: item.categories.map((category) => category.id),
  };
}

export function toBoardGameRequest(boardGame: BoardGame): BoardGameRequest {
  const itemRequest = toRequest(boardGame);
  return {
    ...itemRequest,
    minPlayers: boardGame.minPlayers,
    maxPlayers: boardGame.maxPlayers,
    playTime: boardGame.playTime,
    complexity: boardGame.complexity,
    types: boardGame.types,
    baseGameId: boardGame.baseGame ? boardGame.baseGame.id : undefined,
  };
}

export function toBookRequest(book: Book): BookRequest {
  const itemRequest = toRequest(book);
  return {
    ...itemRequest,
    author: book.author,
    isbn: book.isbn,
    type: book.type,
    sagaName: book.saga ? book.saga : undefined,
  };
}

export function toMovieRequest(movie: Movie): MovieRequest {
  const itemRequest = toRequest(movie);
  return {
    ...itemRequest,
    format: toFormatValue(movie.format),
    numberOfDiscs: movie.numberOfDiscs,
    releaseDate: movie.releaseDate,
    trailerUrl: movie.trailerUrl,
    sagaName: movie.saga ? movie.saga.name : undefined,
  };
}

export function toRolGameRequest(rolGame: RolGame): RolGameRequest {
  const itemRequest = toRequest(rolGame);
  return {
    ...itemRequest,
    sagaId: rolGame.saga.id,
    type: rolGame.type,
  };
}

export function toRolSagaRequest(rolSaga: RolSaga): RolSagaRequest {
  return {
    name: rolSaga.name,
    description: rolSaga.description,
    website: rolSaga.website,
    characterSheetUrl: rolSaga.characterSheetUrl,
    gameMaster: rolSaga.gameMaster,
    dice: rolSaga.dice,
    recommendedPlayers: rolSaga.recommendedPlayers,
    sectionId: rolSaga.section.id,
    categoriesIds: rolSaga.categories.map((category) => category.id),
  };
}

export function toSeriesRequest(series: Series): SeriesRequest {
  const itemRequest = toRequest(series);
  return {
    ...itemRequest,
    format: toFormatValue(series.format),
    numberOfDiscs: series.numberOfDiscs,
    releaseDate: series.releaseDate,
    numberOfSeasons: series.numberOfSeasons,
    status: series.status,
    seasons: series.seasons,
  };
}

export function toVideoGameRequest(videoGame: VideoGame): VideoGameRequest {
  const itemRequest = toRequest(videoGame);
  return {
    ...itemRequest,
    platform: videoGame.platform,
    releaseDate: videoGame.releaseDate,
    trailerUrl: videoGame.trailerUrl,
  };
}

export function parseItemCondition(condition: ItemCondition): string {
  switch (condition) {
    case "PERFECT":
      return "Perfecto";
    case "MINOR_DAMAGE":
      return "Daño menor";
    case "MODERATE_DAMAGE":
      return "Daño moderado";
    case "SEVERE_DAMAGE":
      return "Daño severo";
  }
}

export function parsePrice(price: number): string {
  return price.toLocaleString("es-ES", {
    style: "currency",
    currency: "EUR",
  });
}

export function getPlaceholder(itemType: ItemType): string {
  switch (itemType) {
    case ITEM_TYPES.BOARD_GAME:
      return PLACEHOLDER.BOARDGAME;
    case ITEM_TYPES.BOOK:
      return PLACEHOLDER.BOOK;
    case ITEM_TYPES.MOVIE:
      return PLACEHOLDER.MOVIE;
    case ITEM_TYPES.ROL_GAME:
      return PLACEHOLDER.ROLGAME;
    case ITEM_TYPES.SERIES:
      return PLACEHOLDER.SERIES;
    case ITEM_TYPES.VIDEO_GAME:
      return PLACEHOLDER.VIDEOGAME;
  }
}
