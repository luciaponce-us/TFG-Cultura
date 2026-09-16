import { VIDEOGAME_ROUTES } from "../routes";
import type { Paginated } from "@/modules/core/types";
import type { VideoGame, VideoGameRequest } from "../types/videogame";
import { createItem, fetchAllItems } from "./item.service";

export async function fetchAllVideoGames(
  page: number = 0,
  size: number = 10,
  nameContains?: string,
  categories?: string[],
): Promise<Paginated<VideoGame>> {
  return fetchAllItems<VideoGame>(
    VIDEOGAME_ROUTES,
    page,
    size,
    nameContains,
    categories,
  );
}

export async function createVideoGame(
  request: VideoGameRequest,
  image: File | null,
  token: string,
): Promise<VideoGame> {
  return createItem<VideoGame, VideoGameRequest>(
    VIDEOGAME_ROUTES,
    token,
    request,
    image,
  );
}
