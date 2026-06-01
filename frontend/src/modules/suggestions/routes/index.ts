import { API_BASE_URL } from "@/modules/core/utils/utils";

const SUGGESTIONS_BASE_URL = `${API_BASE_URL}/api/suggestions`;

export const SUGGESTION_ROUTES = {
  GET_ALL: `${SUGGESTIONS_BASE_URL}`,
  CREATE: `${SUGGESTIONS_BASE_URL}/create`,
};
