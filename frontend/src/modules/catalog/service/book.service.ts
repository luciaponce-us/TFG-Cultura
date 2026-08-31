import {
  fetchWithTimeout,
  handleResponse,
  authHeaders,
} from "@/modules/core/utils/utils";

import type { Book } from "../types";

import { BOOK_ROUTES } from "../routes";

import type { Paginated } from "@/modules/core/types";

export async function fetchAllBooks(
  page: number = 0,
  size: number = 10,
  nameContains?: string,
  categories?: string[],
  token?: string | null,
): Promise<Paginated<Book>> {
  let queryParams = `?page=${page}&size=${size}`;

  if (nameContains) queryParams += `&nameContains=${encodeURIComponent(nameContains)}`;
  if (categories && categories.length > 0) queryParams += `&categories=${categories.join(",")}`;

  const res = await fetchWithTimeout(
    `${BOOK_ROUTES.GET_ALL_BOOKS}${queryParams}`,
    {
      method: "GET",
      headers: token ? authHeaders(token) : {},
    },
  );

  return handleResponse<Paginated<Book>>(res);
}