import type { Paginated } from "@/modules/core/types";
import type { Series, SeriesRequest } from "../types/series";
import { SERIES_ROUTES } from "../routes";
import { createItem, fetchAllItems } from "./item.service";

export async function fetchAllSeries(
  page: number = 0,
  size: number = 10,
  nameContains?: string,
  categories?: string[],
): Promise<Paginated<Series>> {
  return fetchAllItems<Series>(SERIES_ROUTES, page, size, nameContains, categories);
}

export async function createSeries(
  token: string,
  series: SeriesRequest,
  image: File | null,
): Promise<Series> {
  return createItem<Series, SeriesRequest>(
    SERIES_ROUTES,
    token,
    series,
    image
  );
}
