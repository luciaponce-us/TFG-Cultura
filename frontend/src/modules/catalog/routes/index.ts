import { API_BASE_URL } from "@/modules/core/utils/utils";

export const BOOK_ROUTES = {
  GET_ALL_BOOKS: `${API_BASE_URL}/api/catalog/books`,
  GET_BOOK_BY_ID: (id: string) => `${API_BASE_URL}/api/catalog/books/${id}`,
};

export const SAGA_ROUTES = {
  GET_ALL_SAGAS: `${API_BASE_URL}/api/catalog/sagas`,
};
