import type { Paginated } from "@/modules/core/types";
import type { RolSaga, RolSagaRequest } from "../types/rolgame";
import { ROL_SAGA_ROUTES } from "../routes";
import {
  fetchWithTimeout,
  handleResponse,
  authHeaders,
  removeEmptyFields,
} from "@/modules/core/utils/utils";

export async function fetchAllRolSagas(
  page: number = 0,
  size: number = 10,
): Promise<Paginated<RolSaga>> {
  const queryParams = `?page=${page}&size=${size}`;
  const res = await fetchWithTimeout(ROL_SAGA_ROUTES.BASE + queryParams, {
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

export async function createRolSaga(
  request: RolSagaRequest,
  token: string,
  image: File | null,
): Promise<RolSaga> {
  const formData = new FormData();

  formData.append(
    "rolSaga",
    new Blob([JSON.stringify(removeEmptyFields(request))], {
      type: "application/json",
    }),
  );

  if (image) {
    formData.append("image", image);
  }

  const res = await fetchWithTimeout(ROL_SAGA_ROUTES.BASE, {
    method: "POST",
    headers: {
      ...authHeaders(token),
    },
    body: formData,
  });

  return handleResponse<RolSaga>(res);
}

export async function updateRolSaga(
  token: string,
  sagaId: string,
  request: RolSagaRequest,
  image: File | null,
): Promise<RolSaga> {
  const formData = new FormData();

  formData.append(
    "rolSaga",
    new Blob([JSON.stringify(removeEmptyFields(request))], {
      type: "application/json",
    }),
  );

  if (image) {
    formData.append("image", image);
  }

  const res = await fetchWithTimeout(ROL_SAGA_ROUTES.GET_BY_ID(sagaId), {
    method: "PUT",
    headers: {
      ...authHeaders(token),
    },
    body: formData,
  });

  return handleResponse<RolSaga>(res);
}

export async function deleteRolSaga(
  token: string,
  sagaId: string,
): Promise<void> {
  const res = await fetchWithTimeout(ROL_SAGA_ROUTES.GET_BY_ID(sagaId), {
    method: "DELETE",
    headers: {
      ...authHeaders(token),
    },
  });

  return handleResponse<void>(res);
}
