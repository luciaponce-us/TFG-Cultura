import { API_BASE_URL } from "../../core/utils/utils";

export const USER_ROUTES = {
  REGISTER: `${API_BASE_URL}/api/users/auth/register`,
  LOGIN: `${API_BASE_URL}/api/users/auth/login`,
  PROFILE: `${API_BASE_URL}/api/users/profile`,
  GET_ALL: `${API_BASE_URL}/api/users`,
  GET_BY_USERNAME: (username: string) =>
    `${API_BASE_URL}/api/users/${username}`,
  DELETE: (username: string) => `${API_BASE_URL}/api/users/${username}`,
  EDIT_USER: (username: string) => `${API_BASE_URL}/api/users/${username}`,
  EDIT_USER_AVATAR: (username: string) =>
    `${API_BASE_URL}/api/users/${username}/avatar`,
};
