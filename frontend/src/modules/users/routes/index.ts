import { API_BASE_URL } from "../../core/utils/utils";

const USERS_BASE_URL = `${API_BASE_URL}/api/users`;

export const USER_ROUTES = {
  REGISTER: `${USERS_BASE_URL}/auth/register`,
  LOGIN: `${USERS_BASE_URL}/auth/login`,
  PROFILE: `${USERS_BASE_URL}/profile`,
  GET_ALL: `${USERS_BASE_URL}`,
  GET_BY_USERNAME: (username: string) => `${USERS_BASE_URL}/${username}`,
  DELETE: (username: string) => `${USERS_BASE_URL}/${username}`,
  EDIT_USER: (username: string) => `${USERS_BASE_URL}/${username}`,
  EDIT_USER_AVATAR: (username: string) =>
    `${USERS_BASE_URL}/${username}/avatar`,
  ACTIVATE_USER: (username: string) => `${USERS_BASE_URL}/${username}/activate`,
  DEACTIVATE_USER: (username: string) =>
    `${USERS_BASE_URL}/${username}/deactivate`,
};
