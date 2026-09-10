import {
  fetchWithTimeout,
  handleResponse,
  jsonHeaders,
  authHeaders,
} from "@/modules/core/utils/utils";

import type { Saga } from "../types";

import { SAGA_ROUTES } from "../routes";

export async function fetchAllSagas(): Promise<Saga[]> {
  const res = await fetchWithTimeout(SAGA_ROUTES.GET_ALL, {
    method: "GET",
  });

  return handleResponse<Saga[]>(res);
}

export async function createSaga(token: string, name: string): Promise<Saga> {
  const res = await fetchWithTimeout(SAGA_ROUTES.GET_ALL, {
    method: "POST",
    headers: { ...jsonHeaders, ...authHeaders(token) },
    body: name,
  });

  return handleResponse<Saga>(res);
}
