import type { Paginated } from "@/modules/core/types";
import type { RolSaga } from "../types/rolgame";
import { ROL_SAGA_ROUTES } from "../routes";
import { fetchWithTimeout, handleResponse } from "@/modules/core/utils/utils";

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
