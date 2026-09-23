import { fetchWithTimeout, handleResponse } from "@/modules/core/utils/utils";

import type { Section } from "../types";

import { SECTION_ROUTES } from "../routes";

export async function fetchAllSections(): Promise<Section[]> {
  const res = await fetchWithTimeout(SECTION_ROUTES.GET_ALL, {
    method: "GET",
  });

  return handleResponse<Section[]>(res);
}
