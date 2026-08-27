export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL as string;
import type { ChangeEvent, Dispatch, SetStateAction } from "react";
import { ApiError } from "../types";
import { useBreakpointValue } from "@chakra-ui/react/hooks";
import { toaster } from "../components";

export const jsonHeaders = { "Content-Type": "application/json" };
export const authHeaders = (token: string) => ({
  Authorization: `Bearer ${token}`,
});
const REQUEST_TIMEOUT_MS = 12000;

export async function handleResponse<T>(res: Response): Promise<T> {
  const contentType = res.headers.get("content-type") ?? "";

  if (!res.ok) {
    let message = `Error ${res.status}`;
    let errors: { [key: string]: string } = {};

    try {
      if (contentType.includes("application/json")) {
        const data: unknown = await res.json();

        if (typeof data === "object" && data !== null) {
          const d = data as Partial<ApiError>;
          message = d.message || message;
          errors = d.errors || {};
        }
      } else {
        const text = await res.text();
        message = text || message;
      }
    } catch {
      // ignore parsing errors
    }
    const apiError = new ApiError(message, res.status, errors);

    throw apiError;
  }

  if (!contentType.includes("application/json")) {
    // Respuesta en formano no json
    const text = await res.text();
    return text as unknown as T;
  }

  const text = await res.text();
  if (!text) return {} as T;

  return JSON.parse(text) as T;
}

export async function fetchWithTimeout(
  input: RequestInfo | URL,
  init: RequestInit,
): Promise<Response> {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS);

  try {
    return await fetch(input, { ...init, signal: controller.signal });
  } catch (error) {
    if (error instanceof Error && error.name === "AbortError") {
      const message =
        "Tiempo de espera del servidor agotado. Vuelve a intentarlo más tarde.";
      throw new ApiError(message, 500);
    } else {
      throw error;
    }
  } finally {
    clearTimeout(timeoutId);
  }
}

export function isApiError(err: unknown): err is ApiError {
  if (err == null || err == undefined) return false;
  return (
    typeof err === "object" &&
    "status" in err &&
    "message" in err &&
    "timestamp" in err
  );
}

export function isDeactivatedUserError(err: unknown): boolean {
  if (!isApiError(err)) return false;
  return err.status === 403 && err.message.includes("desactivado");
}

export function throwDeactivatedUserError(err: ApiError): void {
  console.error("Error de autorización al eliminar sugerencia:", err.message);
  toaster.create({
    title: "No autorizado",
    description:
      "No tienes permiso para realizar esta acción. Tu usuario está desactivado.",
    type: "error",
  });
}

export const handleChange = <
  T extends object,
  E extends Partial<Record<keyof T, string>>,
>(
  e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  form: T,
  setErrors: Dispatch<SetStateAction<E>>,
  setForm: Dispatch<SetStateAction<T>>,
) => {
  setErrors({} as E);
  setForm({
    ...form,
    [e.target.name]: e.target.value,
  });
};

export const handleSelectChange = <
  T extends object,
  E extends Partial<Record<keyof T, string>>,
>(
  value: string[],
  name: keyof T,
  form: T,
  setErrors: Dispatch<SetStateAction<E>>,
  setForm: Dispatch<SetStateAction<T>>,
) => {
  setErrors({} as E);
  setForm({
    ...form,
    [name]: value[0],
  });
};

export function useIsMobile() {
  return useBreakpointValue({ base: true, md: false });
}
