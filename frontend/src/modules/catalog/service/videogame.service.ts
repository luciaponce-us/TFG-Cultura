import { authHeaders, fetchWithTimeout, handleResponse, removeEmptyFields } from "@/modules/core/utils/utils";

import { VIDEOGAME_ROUTES } from "../routes";
import type { Paginated } from "@/modules/core/types";
import type { VideoGame, VideoGameRequest } from "../types/videogame";

export async function fetchAllVideoGames(
  page: number = 0,
  size: number = 10,
  nameContains?: string,
  categories?: string[],
): Promise<Paginated<VideoGame>> {
  let queryParams = `?page=${page}&size=${size}`;

  if (nameContains)
    queryParams += `&nameContains=${encodeURIComponent(nameContains)}`;

  if (categories && categories.length > 0)
    queryParams += `&categories=${categories.join(",")}`;

  const res = await fetchWithTimeout(
    `${VIDEOGAME_ROUTES.GET_ALL}${queryParams}`,
    {
      method: "GET",
    },
  );

  return handleResponse<Paginated<VideoGame>>(res);
}

export async function createVideoGame(request: VideoGameRequest, image: File | null, token: string): Promise<VideoGame> {
  const formData = new FormData();
  formData.append(
    "item",
    new Blob([JSON.stringify(removeEmptyFields(request))], {
      type: "application/json",
    }),
  );

  if (image) {
    formData.append("image", image);
  }

  const res = await fetchWithTimeout(VIDEOGAME_ROUTES.GET_ALL, {
    method: "POST",
    headers: {...authHeaders(token)},
    body: formData,
  });

  return handleResponse<VideoGame>(res);
}