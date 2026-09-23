import { API_BASE_URL } from "@/modules/core/utils/utils";
import type { ItemRoutes } from "../types";

interface BookRoutes extends ItemRoutes {
  GET_ALL_BY_TYPE: (types: string[]) => string;
}

export const BOOK_ROUTES: BookRoutes = {
  BASE: `${API_BASE_URL}/api/catalog/books`,
  GET_ALL_BY_TYPE: (types: string[]) =>
    `${API_BASE_URL}/api/catalog/books/types/${types.join(",")}`,
  GET_BY_ID: (id: string) => `${API_BASE_URL}/api/catalog/books/${id}`,
};

export const SAGA_ROUTES = {
  BASE: `${API_BASE_URL}/api/catalog/sagas`,
  GET_BY_NAME: (name: string) => `${API_BASE_URL}/api/catalog/sagas/${name}`,
  GET_BY_ID: (id: string) => `${API_BASE_URL}/api/catalog/sagas/${id}`,
};

export const MOVIE_ROUTES: ItemRoutes = {
  BASE: `${API_BASE_URL}/api/catalog/movies`,
  GET_BY_ID: (id: string) => `${API_BASE_URL}/api/catalog/movies/${id}`,
};

export const SERIES_ROUTES: ItemRoutes = {
  BASE: `${API_BASE_URL}/api/catalog/series`,
  GET_BY_ID: (id: string) => `${API_BASE_URL}/api/catalog/series/${id}`,
};
export const BOARDGAME_ROUTES: ItemRoutes = {
  BASE: `${API_BASE_URL}/api/catalog/board-games`,
  GET_BY_ID: (id: string) => `${API_BASE_URL}/api/catalog/board-games/${id}`,
};

interface RolGameRoutes extends ItemRoutes {
  GET_BY_SAGA_ID: (sagaId: string) => string;
}

export const ROLGAME_ROUTES: RolGameRoutes = {
  BASE: `${API_BASE_URL}/api/catalog/rol-games`,
  GET_BY_ID: (id: string) => `${API_BASE_URL}/api/catalog/rol-games/${id}`,
  GET_BY_SAGA_ID: (sagaId: string) =>
    `${API_BASE_URL}/api/catalog/rol-games/saga/${sagaId}`,
};

export const ROL_SAGA_ROUTES = {
  BASE: `${API_BASE_URL}/api/catalog/rol-sagas`,
  GET_BY_ID: (id: string) => `${API_BASE_URL}/api/catalog/rol-sagas/${id}`,
};

export const VIDEOGAME_ROUTES: ItemRoutes = {
  BASE: `${API_BASE_URL}/api/catalog/videogames`,
  GET_BY_ID: (id: string) => `${API_BASE_URL}/api/catalog/videogames/${id}`,
};
