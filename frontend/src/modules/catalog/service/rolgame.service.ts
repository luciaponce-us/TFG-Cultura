import { fetchWithTimeout, handleResponse } from "@/modules/core/utils/utils";

import { ROLGAME_ROUTES } from "../routes";
import type { RolGame } from "../types/rolgame";

export async function fetchAllRolGamesBySagaId(
  sagaId: string,
): Promise<RolGame[]> {
  const res = await fetchWithTimeout(ROLGAME_ROUTES.GET_BY_SAGA_ID(sagaId), {
    method: "GET",
  });

  return handleResponse<RolGame[]>(res);
}
