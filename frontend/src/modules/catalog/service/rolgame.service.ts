import { fetchWithTimeout, handleResponse } from "@/modules/core/utils/utils";

import { ROLGAME_ROUTES } from "../routes";
import type { RolGame, RolGameRequest } from "../types/rolgame";
import { createItem, fetchItemById, updateItem } from "./item.service";

export async function fetchAllRolGamesBySagaId(
  sagaId: string,
): Promise<RolGame[]> {
  const res = await fetchWithTimeout(ROLGAME_ROUTES.GET_BY_SAGA_ID(sagaId), {
    method: "GET",
  });

  return handleResponse<RolGame[]>(res);
}

export async function fetchRolGameById(id: string): Promise<RolGame> {
  return fetchItemById<RolGame>(ROLGAME_ROUTES, id);
}

export async function createRolGame(
  token: string,
  rolGame: RolGameRequest,
  image: File | null,
): Promise<RolGame> {
  return createItem<RolGame, RolGameRequest>(
    ROLGAME_ROUTES,
    token,
    rolGame,
    image,
  );
}

export async function updateRolGame(
  token: string,
  rolGameId: string,
  rolGame: RolGameRequest,
  image: File | null,
): Promise<RolGame> {
  return updateItem<RolGame, RolGameRequest>(
    ROLGAME_ROUTES,
    token,
    rolGameId,
    rolGame,
    image,
  );
}
