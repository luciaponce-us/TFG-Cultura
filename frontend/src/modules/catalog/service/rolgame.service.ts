import { fetchWithTimeout, handleResponse } from "@/modules/core/utils/utils";

import { ROLGAME_ROUTES, ROL_SAGA_ROUTES } from "../routes";
import type { RolGame, RolSaga } from "../types/rolgame";
import type { Paginated } from "@/modules/core/types";

export async function fetchAllRolGamesBySagaId(
  sagaId: string,
): Promise<RolGame[]> {
  const res = await fetchWithTimeout(ROLGAME_ROUTES.GET_BY_SAGA_ID(sagaId), {
    method: "GET",
  });

  return handleResponse<RolGame[]>(res);
}

export async function fetchAllRolSagas(
  page: number = 0,
  size: number = 10,
): Promise<Paginated<RolSaga>> {
  const queryParams = `?page=${page}&size=${size}`;
  const res = await fetchWithTimeout(ROL_SAGA_ROUTES.GET_ALL + queryParams, {
    method: "GET",
  });
  return handleResponse<Paginated<RolSaga>>(res);
}

export async function fetchRolSagaById(sagaId: string): Promise<RolSaga> {
  const res = await fetchWithTimeout(ROL_SAGA_ROUTES.GET_BY_ID(sagaId), {
    method: "GET",
  });

  return handleResponse<RolSaga>(res);
}
