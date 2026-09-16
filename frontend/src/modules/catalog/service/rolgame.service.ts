import {
  fetchWithTimeout,
  handleResponse,
} from "@/modules/core/utils/utils";

import { ROLGAME_ROUTES } from "../routes";
import type { RolGame, RolGameRequest } from "../types/rolgame";
import { createItem } from "./item.service";

export async function fetchAllRolGamesBySagaId(
  sagaId: string,
): Promise<RolGame[]> {
  const res = await fetchWithTimeout(ROLGAME_ROUTES.GET_BY_SAGA_ID(sagaId), {
    method: "GET",
  });

  return handleResponse<RolGame[]>(res);
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
    image
  );
}
