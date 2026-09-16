import { authHeaders, fetchWithTimeout, handleResponse, removeEmptyFields } from "@/modules/core/utils/utils";

import { ROLGAME_ROUTES } from "../routes";
import type { RolGame, RolGameRequest } from "../types/rolgame";

export async function fetchAllRolGamesBySagaId(
  sagaId: string,
): Promise<RolGame[]> {
  const res = await fetchWithTimeout(ROLGAME_ROUTES.GET_BY_SAGA_ID(sagaId), {
    method: "GET",
  });

  return handleResponse<RolGame[]>(res);
}

export async function createRolGame(token: string, rolGame: RolGameRequest, image: File | null): Promise<RolGame> {
    const formData = new FormData();
  
    formData.append(
      "item",
      new Blob([JSON.stringify(removeEmptyFields(rolGame))], {
        type: "application/json",
      }),
    );
  
    if (image) {
      formData.append("image", image);
    }

  const res = await fetchWithTimeout(ROLGAME_ROUTES.GET_ALL, {
    method: "POST",
    headers: authHeaders(token),
    body: formData,
  });

  return handleResponse<RolGame>(res);
}
