import {
  fetchWithTimeout,
  handleResponse,
  authHeaders,
  jsonHeaders,
} from "@/modules/core/utils/utils";

import type { Category, CategoryCreateRequest } from "../types";

import { CATEGORY_ROUTES } from "../routes";

export async function fetchAllCategories(): Promise<Category[]> {
  const res = await fetchWithTimeout(CATEGORY_ROUTES.GET_ALL, {
    method: "GET",
  });

  return handleResponse<Category[]>(res);
}

export async function fetchCategoryById(
  categoryId: string,
): Promise<Category> {
  const res = await fetchWithTimeout(
    CATEGORY_ROUTES.GET_BY_ID(categoryId),
    {
      method: "GET",
    },
  );

  return handleResponse<Category>(res);
}

export async function createCategory(
  category: CategoryCreateRequest,
  token: string,
): Promise<Category> {
  const res = await fetchWithTimeout(CATEGORY_ROUTES.GET_ALL, {
    method: "POST",
    headers: {
      ...jsonHeaders,
      ...authHeaders(token),
    },
    body: JSON.stringify(category),
  });

  return handleResponse<Category>(res);
}

export async function updateCategory(
  categoryId: string,
  category: CategoryCreateRequest,
  token: string,
): Promise<Category> {
  const res = await fetchWithTimeout(
    CATEGORY_ROUTES.GET_BY_ID(categoryId),
    {
      method: "PUT",
      headers: {
        ...jsonHeaders,
        ...authHeaders(token),
      },
      body: JSON.stringify(category),
    },
  );

  return handleResponse<Category>(res);
}

export async function deleteCategory(
  categoryId: string,
  token: string,
): Promise<void> {
  const res = await fetchWithTimeout(
    CATEGORY_ROUTES.GET_BY_ID(categoryId),
    {
      method: "DELETE",
      headers: {
        ...authHeaders(token),
      },
    },
  );

  return handleResponse<void>(res);
}