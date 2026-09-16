import { fetchWithTimeout, handleResponse } from "@/modules/core/utils/utils";
import type { Book, BookRequest, BookType } from "../types/book";
import { BOOK_ROUTES } from "../routes";
import type { Paginated } from "@/modules/core/types";
import { createItem } from "./item.service";

export async function fetchAllBooks(
  page: number = 0,
  size: number = 10,
  types: BookType[],
  nameContains?: string,
  categories?: string[],
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
    },
  );

  return handleResponse<Paginated<Book>>(res);
}

export async function createBook(
  token: string,
  book: BookRequest,
  image: File | null,
): Promise<Book> {
  return createItem<Book, BookRequest>(BOOK_ROUTES, token, book, image);
}
