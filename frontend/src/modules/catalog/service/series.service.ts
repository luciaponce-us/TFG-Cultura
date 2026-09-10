import {
  authHeaders,
  fetchWithTimeout,
  handleResponse,
  removeEmptyFields,
} from "@/modules/core/utils/utils";
import type { Paginated } from "@/modules/core/types";
import type { Series, SeriesRequest } from "../types/series";
import { SERIES_ROUTES } from "../routes";

export async function fetchAllSeries(
  page: number = 0,
  size: number = 10,
  nameContains?: string,
  categories?: string[],
): Promise<Paginated<Series>> {
  let queryParams = `?page=${page}&size=${size}`;

  if (nameContains) {
    queryParams += `&nameContains=${encodeURIComponent(nameContains)}`;
  }
  if (categories && categories.length > 0) {
    queryParams += `&categories=${categories.join(",")}`;
  }

  const res = await fetchWithTimeout(`${SERIES_ROUTES.GET_ALL}${queryParams}`, {
    method: "GET",
  });

  return handleResponse<Paginated<Series>>(res);
}

export async function createSeries(
  token: string,
  series: SeriesRequest,
  image: File | null,
): Promise<Series> {
  const formData = new FormData();

  formData.append(
    "item",
    new Blob([JSON.stringify(removeEmptyFields(series))], {
      type: "application/json",
    }),
  );

  if (image) {
    formData.append("image", image);
  }

  const res = await fetchWithTimeout(SERIES_ROUTES.GET_ALL, {
    method: "POST",
    headers: token ? authHeaders(token) : {},
    body: formData,
  });

  return handleResponse<Series>(res);
}
