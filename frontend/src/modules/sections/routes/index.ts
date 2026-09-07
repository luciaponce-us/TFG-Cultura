import { API_BASE_URL } from "@/modules/core/utils/utils";

export const SECTION_ROUTES = {
  GET_ALL: `${API_BASE_URL}/api/sections`,
  GET_BY_ID: (id: string) => `${API_BASE_URL}/api/sections/${id}`,
};
