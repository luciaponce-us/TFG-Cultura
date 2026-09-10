import {
  fetchWithTimeout,
  handleResponse,
  authHeaders,
} from "@/modules/core/utils/utils";

import type { Book, BookCreateRequest, BookType } from "../types/book";

import { BOOK_ROUTES } from "../routes";

import type { Paginated } from "@/modules/core/types";

function removeEmptyFields<T extends object>(value: T): Partial<T> {
  return Object.fromEntries(
    Object.entries(value).filter(([, fieldValue]) => {
      if (
        fieldValue === null ||
        fieldValue === undefined ||
        fieldValue === ""
      ) {
        return false;
      }

      return !Array.isArray(fieldValue) || fieldValue.length > 0;
    }),
  ) as Partial<T>;
}

export async function fetchAllBooks(
  page: number = 0,
  size: number = 10,
  types: BookType[],
  nameContains?: string,
  categories?: string[],
  token?: string | null,
): Promise<Paginated<Book>> {
  let queryParams = `?page=${page}&size=${size}`;

  if (nameContains)
    queryParams += `&nameContains=${encodeURIComponent(nameContains)}`;
  if (categories && categories.length > 0)
    queryParams += `&categories=${categories.join(",")}`;

  const res = await fetchWithTimeout(
    `${BOOK_ROUTES.GET_ALL_BY_TYPE(types)}${queryParams}`,
    {
      method: "GET",
      headers: token ? authHeaders(token) : {},
    },
  );

  return handleResponse<Paginated<Book>>(res);
}

export async function createBook(
  token: string,
  book: BookCreateRequest,
  image: File | null,
): Promise<Book> {
  const formData = new FormData();

  formData.append(
    "item",
    new Blob([JSON.stringify(removeEmptyFields(book))], {
      type: "application/json",
    }),
  );

  if (image) {
    formData.append("image", image);
  }

  const res = await fetchWithTimeout(BOOK_ROUTES.GET_ALL_BOOKS, {
    method: "POST",
    headers: token ? authHeaders(token) : {},
    body: formData,
  });

  return handleResponse<Book>(res);
}
