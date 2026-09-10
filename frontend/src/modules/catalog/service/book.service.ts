import {
  fetchWithTimeout,
  handleResponse,
  authHeaders,
  removeEmptyFields,
} from "@/modules/core/utils/utils";
import type { Book, BookRequest, BookType } from "../types/book";
import { BOOK_ROUTES } from "../routes";
import type { Paginated } from "@/modules/core/types";


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
  book: BookRequest,
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

  const res = await fetchWithTimeout(BOOK_ROUTES.GET_ALL, {
    method: "POST",
    headers: token ? authHeaders(token) : {},
    body: formData,
  });

  return handleResponse<Book>(res);
}
