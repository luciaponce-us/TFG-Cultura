import { VIDEOGAME_ROUTES } from "../routes";
import type { Paginated } from "@/modules/core/types";
import type { VideoGame, VideoGameRequest } from "../types/videogame";
import {
  createItem,
  deleteItem,
  fetchAllItems,
  fetchItemById,
  updateItem,
} from "./item.service";

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

export async function fetchVideoGameById(
  videoGameId: string,
): Promise<VideoGame> {
  return fetchItemById<VideoGame>(VIDEOGAME_ROUTES, videoGameId);
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

export async function updateVideoGame(
  token: string,
  videoGameId: string,
  request: VideoGameRequest,
  image: File | null,
): Promise<VideoGame> {
  return updateItem<VideoGame, VideoGameRequest>(
    VIDEOGAME_ROUTES,
    token,
    videoGameId,
    request,
    image,
  );
}

export async function deleteVideoGame(
  token: string,
  videoGameId: string,
): Promise<void> {
  return deleteItem(VIDEOGAME_ROUTES, token, videoGameId);
}
