import { fetchWithTimeout, handleResponse } from "@/modules/core/utils/utils";

import type { Category } from "../types";

import { CATEGORY_ROUTES } from "../routes";

export async function fetchAllCategories(): Promise<Category[]> {
  const res = await fetchWithTimeout(CATEGORY_ROUTES.GET_ALL, {
    method: "GET",
  });

  return handleResponse<Category[]>(res);
}
