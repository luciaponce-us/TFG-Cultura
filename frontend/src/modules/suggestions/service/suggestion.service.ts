import { fetchWithTimeout, handleResponse } from "@/modules/core/utils/utils";

import type { Suggestion, SuggestionType } from "../types";
import { SUGGESTION_ROUTES } from "../routes";
import type { Paginated } from "@/modules/core/types";

export async function fetchAllSuggestions(
  page: number = 0,
  limit: number = 10,
  type?: SuggestionType,
  text?: string,
  orderByCreationDate?: boolean,
  supportedByAdmins?: boolean,
): Promise<Paginated<Suggestion>> {
  let queryParams = `?page=${page}&limit=${limit}`;

  if (type) queryParams += `&type=${type}`;
  if (text) queryParams += `&text=${encodeURIComponent(text)}`;
  if (orderByCreationDate !== undefined)
    queryParams += `&orderByCreationDate=${orderByCreationDate}`;
  if (supportedByAdmins !== undefined)
    queryParams += `&supportedByAdmins=${supportedByAdmins}`;
  const res = await fetchWithTimeout(
    `${SUGGESTION_ROUTES.GET_ALL}${queryParams}`,
    {
      method: "GET",
    },
  );

  return handleResponse<Paginated<Suggestion>>(res);
}
