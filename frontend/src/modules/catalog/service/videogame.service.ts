import {
  fetchWithTimeout,
  handleResponse
} from "@/modules/core/utils/utils";

import { VIDEOGAME_ROUTES } from "../routes";
import type { Paginated } from "@/modules/core/types";
import type { VideoGame } from "../types/videogame";

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