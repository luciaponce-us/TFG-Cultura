import type { Movie, MovieRequest } from "../types/movie";
import { MOVIE_ROUTES } from "../routes";
import type { Paginated } from "@/modules/core/types";
import {
  createItem,
  fetchAllItems,
  fetchItemById,
  updateItem,
} from "./item.service";

export async function fetchAllMovies(
  page: number = 0,
  size: number = 10,
  nameContains?: string,
  categories?: string[],
): Promise<Paginated<Movie>> {
  return fetchAllItems<Movie>(
    MOVIE_ROUTES,
    page,
    size,
    nameContains,
    categories,
  );
}

export async function fetchMovieById(id: string): Promise<Movie> {
  return fetchItemById<Movie>(MOVIE_ROUTES, id);
}

export async function createMovie(
  token: string,
  movie: MovieRequest,
  image: File | null,
): Promise<Movie> {
  return createItem<Movie, MovieRequest>(MOVIE_ROUTES, token, movie, image);
}

export async function updateMovie(
  token: string,
  id: string,
  movie: MovieRequest,
  image: File | null,
): Promise<Movie> {
  return updateItem<Movie, MovieRequest>(MOVIE_ROUTES, token, id, movie, image);
}
