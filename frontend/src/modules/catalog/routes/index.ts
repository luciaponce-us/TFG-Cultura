import { API_BASE_URL } from "@/modules/core/utils/utils";

export const BOOK_ROUTES = {
  GET_ALL: `${API_BASE_URL}/api/catalog/books`,
  GET_ALL_BY_TYPE: (types: string[]) =>
    `${API_BASE_URL}/api/catalog/books/types/${types.join(",")}`,
  GET_BY_ID: (id: string) => `${API_BASE_URL}/api/catalog/books/${id}`,
};

export const SAGA_ROUTES = {
  GET_ALL: `${API_BASE_URL}/api/catalog/sagas`,
};

export const MOVIE_ROUTES = {
  GET_ALL: `${API_BASE_URL}/api/catalog/movies`,
  GET_BY_ID: (id: string) => `${API_BASE_URL}/api/catalog/movies/${id}`,
};

export const SERIES_ROUTES = {
  GET_ALL: `${API_BASE_URL}/api/catalog/series`,
  GET_BY_ID: (id: string) => `${API_BASE_URL}/api/catalog/series/${id}`,
};

export const BOARDGAME_ROUTES = {
  GET_ALL: `${API_BASE_URL}/api/catalog/board-games`,
  GET_BY_ID: (id: string) => `${API_BASE_URL}/api/catalog/board-games/${id}`
}