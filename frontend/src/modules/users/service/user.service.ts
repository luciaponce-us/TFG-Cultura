import {
  fetchWithTimeout,
  handleResponse,
  jsonHeaders,
} from "../../core/utils/utils";
import { USER_ROUTES } from "../routes";
import type { User, UserRegisterRequest } from "../types";

export async function registerUser(data: UserRegisterRequest): Promise<User> {
  const res = await fetchWithTimeout(USER_ROUTES.REGISTER, {
    method: "POST",
    headers: jsonHeaders,
    body: JSON.stringify(data),
  });

  return handleResponse<User>(res);
}
