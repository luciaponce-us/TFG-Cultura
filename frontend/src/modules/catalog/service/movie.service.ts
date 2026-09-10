import {
  fetchWithTimeout,
  handleResponse,
  authHeaders,
  removeEmptyFields,
} from "@/modules/core/utils/utils";

import type { Movie, MovieRequest } from "../types/movie";
import { MOVIE_ROUTES } from "../routes";
import type { Paginated } from "@/modules/core/types";

export async function fetchAllMovies(
  page: number = 0,
  size: number = 10,
  nameContains?: string,
  categories?: string[]
): Promise<Paginated<Movie>> {
  let queryParams = `?page=${page}&size=${size}`;

  if (nameContains)
    queryParams += `&nameContains=${encodeURIComponent(nameContains)}`;
  if (categories && categories.length > 0)
    queryParams += `&categories=${categories.join(",")}`;

  const res = await fetchWithTimeout(
    `${MOVIE_ROUTES.GET_ALL}${queryParams}`,
    {
      method: "GET"
    },
  );

  return handleResponse<Paginated<Movie>>(res);
}

export async function createMovie(
  token: string,
  movie: MovieRequest,
  image: File | null
): Promise<Movie> {
  const formData = new FormData();

  formData.append(
    "item",
    new Blob([JSON.stringify(removeEmptyFields(movie))], {
      type: "application/json",
    }),
  );

  if (image) {
    formData.append("image", image);
  }

  const res = await fetchWithTimeout(MOVIE_ROUTES.GET_ALL, {
    method: "POST",
    headers: token ? authHeaders(token) : {},
    body: formData,
  });

  return handleResponse<Movie>(res);
}