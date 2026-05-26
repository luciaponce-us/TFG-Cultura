import {
  fetchWithTimeout,
  handleResponse,
  jsonHeaders,
} from "../../core/utils/utils";
import { USER_ROUTES } from "../routes";
import type { User, UserRegisterRequest, UserLoginRequest, UserUpdateRequest } from "../types";
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

export async function getUserByUsername(token: string, username: string): Promise<User> {
  const res = await fetchWithTimeout(USER_ROUTES.GET_BY_USERNAME(username), {
    method: "GET",
    headers: { ...jsonHeaders, Authorization: `Bearer ${token}` },
  });

  return handleResponse<User>(res);
}

export async function deleteUser(token: string, username: string): Promise<void> {
  const res = await fetchWithTimeout(USER_ROUTES.DELETE(username), {
    method: "DELETE",
    headers: { ...jsonHeaders, Authorization: `Bearer ${token}` },
  });

  return handleResponse<void>(res);
}

export async function updateUser(
  token: string,
  username: string,
  userData: UserUpdateRequest,
): Promise<User> {

  const res = await fetchWithTimeout(USER_ROUTES.EDIT_USER(username), {
    method: "PUT",
    headers: { ...jsonHeaders, Authorization: `Bearer ${token}` },
    body: JSON.stringify(userData),
  });

  return handleResponse<User>(res);
}

export async function updateUserAvatar(
  token: string,
  username: string,
  avatar: File,
): Promise<User> {
  const formData = new FormData();
  formData.append("avatar", avatar);

  const res = await fetchWithTimeout(USER_ROUTES.EDIT_USER_AVATAR(username), {
    method: "PUT",
    headers: { Authorization: `Bearer ${token}` },
    body: formData,
  });

  return handleResponse<User>(res);
}
