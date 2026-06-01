import {
  fetchWithTimeout,
  handleResponse,
  jsonHeaders,
  authHeaders,
} from "@/modules/core/utils/utils";
import type {
  Suggestion,
  SuggestionCreateRequest,
  SuggestionType,
} from "../types";
import { SUGGESTION_ROUTES } from "../routes";
import type { Paginated } from "@/modules/core/types";

export async function fetchAllSuggestions(
  page: number = 0,
  limit: number = 10,
  type?: SuggestionType | null | undefined,
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

export async function createSuggestion(
  token: string,
  form: SuggestionCreateRequest,
): Promise<Suggestion> {
  const res = await fetchWithTimeout(SUGGESTION_ROUTES.CREATE, {
    method: "POST",
    headers: {
      ...jsonHeaders,
      Authorization: `Bearer ${token}`,
    },
    body: JSON.stringify(form),
  });

  return handleResponse<Suggestion>(res);
}

export async function supportSuggestion(
  token: string,
  suggestionId: string,
): Promise<Suggestion> {
  const res = await fetchWithTimeout(SUGGESTION_ROUTES.SUPPORT(suggestionId), {
    method: "PUT",
    headers: {
      ...authHeaders(token),
    },
  });

  return handleResponse<Suggestion>(res);
}

export async function unsupportSuggestion(
  token: string,
  suggestionId: string,
): Promise<Suggestion> {
  const res = await fetchWithTimeout(
    SUGGESTION_ROUTES.UNSUPPORT(suggestionId),
    {
      method: "PUT",
      headers: {
        ...authHeaders(token),
      },
    },
  );

  return handleResponse<Suggestion>(res);
}
