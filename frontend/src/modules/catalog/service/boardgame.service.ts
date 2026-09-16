import type { Paginated } from "@/modules/core/types";
import { BOARDGAME_ROUTES } from "../routes";
import type { BoardGame, BoardGameRequest } from "../types/boardgame";
import { fetchAllItems, createItem } from "./item.service";

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
