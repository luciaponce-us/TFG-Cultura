import { fetchWithTimeout, handleResponse } from "../../core/utils/utils";
import { USER_ROUTES } from "../routes";
import type { User, UserRegisterRequest } from "../types";

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
