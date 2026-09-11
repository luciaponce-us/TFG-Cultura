import {
  fetchWithTimeout,
  handleResponse,
  authHeaders,
  removeEmptyFields,
} from "@/modules/core/utils/utils";
import type { BoardGame, BoardGameRequest } from "../types/boardgame";
import { BOARDGAME_ROUTES } from "../routes";
import type { Paginated } from "@/modules/core/types";

export async function fetchAllBoardGames(
  page: number = 0,
  size: number = 10,
  nameContains?: string,
  categories?: string[],
): Promise<Paginated<BoardGame>> {
  let queryParams = `?page=${page}&size=${size}`;

  if (nameContains)
    queryParams += `&nameContains=${encodeURIComponent(nameContains)}`;
  if (categories && categories.length > 0)
    queryParams += `&categories=${categories.join(",")}`;

  const res = await fetchWithTimeout(
    `${BOARDGAME_ROUTES.GET_ALL}${queryParams}`,
    {
      method: "GET",
    },
  );

  return handleResponse<Paginated<BoardGame>>(res);
}

export async function createBoardGame(
  token: string,
  boardGame: BoardGameRequest,
  image: File | null,
): Promise<BoardGame> {
  const formData = new FormData();

  formData.append(
    "item",
    new Blob([JSON.stringify(removeEmptyFields(boardGame))], {
      type: "application/json",
    }),
  );

  if (image) {
    formData.append("image", image);
  }

  const res = await fetchWithTimeout(BOARDGAME_ROUTES.GET_ALL, {
    method: "POST",
    headers: token ? authHeaders(token) : {},
    body: formData,
  });

  return handleResponse<BoardGame>(res);
}
