import type { Paginated } from "@/modules/core/types";
import type { Series, SeriesRequest } from "../types/series";
import { SERIES_ROUTES } from "../routes";
import {
  createItem,
  deleteItem,
  fetchAllItems,
  fetchItemById,
  updateItem,
} from "./item.service";

export async function fetchAllSeries(
  page: number = 0,
  size: number = 10,
  nameContains?: string,
  categories?: string[],
): Promise<Paginated<Series>> {
  return fetchAllItems<Series>(
    SERIES_ROUTES,
    page,
    size,
    nameContains,
    categories,
  );
}

export async function fetchSeriesById(seriesId: string): Promise<Series> {
  return fetchItemById<Series>(SERIES_ROUTES, seriesId);
}

export async function createSeries(
  token: string,
  series: SeriesRequest,
  image: File | null,
): Promise<Series> {
  return createItem<Series, SeriesRequest>(SERIES_ROUTES, token, series, image);
}

export async function updateSeries(
  token: string,
  seriesId: string,
  series: SeriesRequest,
  image: File | null,
): Promise<Series> {
  return updateItem<Series, SeriesRequest>(
    SERIES_ROUTES,
    token,
    seriesId,
    series,
    image,
  );
}

export async function deleteSeries(
  token: string,
  seriesId: string,
): Promise<void> {
  return deleteItem(SERIES_ROUTES, token, seriesId);
}
