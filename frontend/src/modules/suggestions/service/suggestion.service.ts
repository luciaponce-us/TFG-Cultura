import { fetchWithTimeout, handleResponse } from "@/modules/core/utils/utils";

import type { Suggestion } from "../types";
import { SUGGESTION_ROUTES } from "../routes";

export async function fetchAllSuggestions(): Promise<Suggestion[]> {
  const res = await fetchWithTimeout(SUGGESTION_ROUTES.GET_ALL, {
    method: "GET",
  });

  return handleResponse<Suggestion[]>(res);
}
