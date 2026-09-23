import { fetchWithTimeout, handleResponse } from "@/modules/core/utils/utils";
import type { Book, BookRequest, BookType } from "../types/book";
import { BOOK_ROUTES } from "../routes";
import type { Paginated } from "@/modules/core/types";
import {
  createItem,
  deleteItem,
  fetchItemById,
  updateItem,
} from "./item.service";

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

export async function fetchAllBooksBySaga(sagaId: string): Promise<Book[]> {
  console.log("Fetching books by sagaId:", sagaId);
  const res = await fetchWithTimeout(BOOK_ROUTES.GET_ALL_BY_SAGA(sagaId), {
    method: "GET",
  });

  return handleResponse<Book[]>(res);
}

export async function fetchBookById(bookId: string): Promise<Book> {
  return fetchItemById<Book>(BOOK_ROUTES, bookId);
}

export async function createBook(
  token: string,
  book: BookRequest,
  image: File | null,
): Promise<Book> {
  return createItem<Book, BookRequest>(BOOK_ROUTES, token, book, image);
}

export async function updateBook(
  token: string,
  bookId: string,
  book: BookRequest,
  image: File | null,
): Promise<Book> {
  return updateItem<Book, BookRequest>(BOOK_ROUTES, token, bookId, book, image);
}

export async function deleteBook(token: string, bookId: string): Promise<void> {
  return deleteItem(BOOK_ROUTES, token, bookId);
}
