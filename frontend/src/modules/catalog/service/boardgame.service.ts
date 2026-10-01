import type { Paginated } from "@/modules/core/types";
import { BOARDGAME_ROUTES } from "../routes";
import type { BoardGame, BoardGameRequest } from "../types/boardgame";
import {
  fetchAllItems,
  createItem,
  updateItem,
  fetchItemById,
  deleteItem,
} from "./item.service";

export async function fetchAllBoardGames(
  page: number = 0,
  size: number = 10,
  nameContains?: string,
  categories?: string[],
): Promise<Paginated<BoardGame>> {
  return fetchAllItems<BoardGame>(
    BOARDGAME_ROUTES,
    page,
    size,
    nameContains,
    categories,
  );
}

export async function fetchBoardGameById(
  boardGameId: string,
): Promise<BoardGame> {
  return fetchItemById<BoardGame>(BOARDGAME_ROUTES, boardGameId);
}

export async function createBoardGame(
  token: string,
  boardGame: BoardGameRequest,
  image: File | null,
): Promise<BoardGame> {
  return createItem<BoardGame, BoardGameRequest>(
    BOARDGAME_ROUTES,
    token,
    boardGame,
    image,
  );
}

export async function updateBoardGame(
  token: string,
  boardGameId: string,
  boardGame: BoardGameRequest,
  image: File | null,
): Promise<BoardGame> {
  return updateItem<BoardGame, BoardGameRequest>(
    BOARDGAME_ROUTES,
    token,
    boardGameId,
    boardGame,
    image,
  );
}

export async function deleteBoardGame(
  token: string,
  boardGameId: string,
): Promise<void> {
  return deleteItem(BOARDGAME_ROUTES, token, boardGameId);
}
