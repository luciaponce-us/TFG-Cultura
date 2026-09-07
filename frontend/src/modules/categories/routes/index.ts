import { API_BASE_URL } from "@/modules/core/utils/utils";

export const CATEGORY_ROUTES = {
  GET_ALL: `${API_BASE_URL}/api/categories`,
  GET_BY_ID: (id: string) => `${API_BASE_URL}/api/categories/${id}`,
};
