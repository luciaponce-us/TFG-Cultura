import {
  fetchWithTimeout,
  handleResponse,
  authHeaders,
  removeEmptyFields,
} from "@/modules/core/utils/utils";
import type { Paginated } from "@/modules/core/types";
import type { Item, ItemRequest, ItemRoutes } from "../types";

export async function fetchAllItems<T extends Item>(
  routes: ItemRoutes,
  page: number = 0,
  size: number = 10,
  nameContains?: string,
  categories?: string[],
): Promise<Paginated<T>> {
  let queryParams = `?page=${page}&size=${size}`;

  if (nameContains) {
    queryParams += `&nameContains=${encodeURIComponent(nameContains)}`;
  }

  if (categories && categories.length > 0) {
    queryParams += `&categories=${categories.join(",")}`;
  }

  const res = await fetchWithTimeout(`${routes.BASE}${queryParams}`, {
    method: "GET",
  });

  return handleResponse<Paginated<T>>(res);
}

export async function createItem<T extends Item, R extends ItemRequest>(
  routes: ItemRoutes,
  token: string,
  request: R,
  image: File | null,
): Promise<T> {
  const formData = new FormData();

  formData.append(
    "item",
    new Blob([JSON.stringify(removeEmptyFields(request))], {
      type: "application/json",
    }),
  );

  if (image) {
    formData.append("image", image);
  }

  const res = await fetchWithTimeout(routes.BASE, {
    method: "POST",
    headers: token ? authHeaders(token) : {},
    body: formData,
  });

  return handleResponse<T>(res);
}
