import {
  fetchWithTimeout,
  handleResponse,
  jsonHeaders,
  authHeaders,
} from "@/modules/core/utils/utils";

import type { Saga } from "../types/saga";

import { SAGA_ROUTES } from "../routes";

export async function fetchAllSagas(): Promise<Saga[]> {
  const res = await fetchWithTimeout(SAGA_ROUTES.BASE, {
    method: "GET",
  });

  return handleResponse<Saga[]>(res);
}

export async function fetchSagaByName(name: string): Promise<Saga> {
  const res = await fetchWithTimeout(SAGA_ROUTES.GET_BY_NAME(name), {
    method: "GET",
  });

  return handleResponse<Saga>(res);
}

export async function createSaga(token: string, name: string): Promise<Saga> {
  const res = await fetchWithTimeout(SAGA_ROUTES.BASE, {
    method: "POST",
    headers: { ...jsonHeaders, ...authHeaders(token) },
    body: name,
  });

  return handleResponse<Saga>(res);
}

export async function updateSaga(
  token: string,
  id: string,
  name: string,
): Promise<Saga> {
  const res = await fetchWithTimeout(SAGA_ROUTES.GET_BY_ID(id), {
    method: "PUT",
    headers: { ...jsonHeaders, ...authHeaders(token) },
    body: name,
  });

  return handleResponse<Saga>(res);
}

export async function deleteSaga(token: string, id: string): Promise<void> {
  const res = await fetchWithTimeout(SAGA_ROUTES.GET_BY_ID(id), {
    method: "DELETE",
    headers: authHeaders(token),
  });

  return handleResponse<void>(res);
}
