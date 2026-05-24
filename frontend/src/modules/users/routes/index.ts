import { API_BASE_URL } from "../../core/utils/utils";

export const USER_ROUTES = {
  REGISTER: `${API_BASE_URL}/api/users/auth/register`,
  LOGIN: `${API_BASE_URL}/api/users/auth/login`,
  PROFILE: `${API_BASE_URL}/api/users/profile`,
  GET_ALL: `${API_BASE_URL}/api/users`,
  DELETE: (username: string) => `${API_BASE_URL}/api/users/${username}`,
};
