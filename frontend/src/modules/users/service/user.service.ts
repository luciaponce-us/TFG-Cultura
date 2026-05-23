import {
  fetchWithTimeout,
  handleResponse,
  jsonHeaders,
} from "../../core/utils/utils";
import { USER_ROUTES } from "../routes";
import type { User, UserRegisterRequest, UserLoginRequest } from "../types";
import type { Paginated } from "../../core/types";

export async function registerUser(
  user: UserRegisterRequest,
  paymentReceipt: File,
  avatar?: File,
): Promise<User> {
  const formData = new FormData();

  formData.append(
    "user",
    new Blob([JSON.stringify(user)], { type: "application/json" }),
  );

  if (avatar) {
    formData.append("avatar", avatar);
  }

  formData.append("paymentReceipt", paymentReceipt);

  const res = await fetchWithTimeout(USER_ROUTES.REGISTER, {
    method: "POST",
    body: formData,
  });

  return handleResponse<User>(res);
}

export async function loginUser(request: UserLoginRequest): Promise<string> {
  const res = await fetchWithTimeout(USER_ROUTES.LOGIN, {
    method: "POST",
    headers: jsonHeaders,
    body: JSON.stringify(request),
  });

  return handleResponse<string>(res);
}

export async function getMyProfile(token: string): Promise<User> {
  const res = await fetchWithTimeout(USER_ROUTES.PROFILE, {
    method: "GET",
    headers: { ...jsonHeaders, Authorization: `Bearer ${token}` },
  });

  return handleResponse<User>(res);
}

export async function getAllUsers(
  token: string,
  page: number = 0,
  limit: number = 10,
): Promise<Paginated<User>> {
  const res = await fetchWithTimeout(
    `${USER_ROUTES.GET_ALL}?page=${page}&size=${limit}`,
    {
      method: "GET",
      headers: { ...jsonHeaders, Authorization: `Bearer ${token}` },
    },
  );

  return handleResponse<Paginated<User>>(res);
}
