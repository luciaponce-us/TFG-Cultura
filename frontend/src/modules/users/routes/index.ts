import { API_BASE_URL } from "../../core/utils/utils";

export const USER_ROUTES = {
  REGISTER: `${API_BASE_URL}/api/users/register`,
  LOGIN: `${API_BASE_URL}/api/users/login`,
  PROFILE: `${API_BASE_URL}/api/users/profile`,
  GET_ALL: `${API_BASE_URL}/api/users`,
};
