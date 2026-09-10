export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL as string;
import type { ChangeEvent, Dispatch, SetStateAction } from "react";
import type { ApiError } from "../types";
import { ApiException } from "../types";
import { useBreakpointValue } from "@chakra-ui/react";
import { toaster } from "../components";

export const jsonHeaders = { "Content-Type": "application/json" };
export const authHeaders = (token: string) => ({
  Authorization: `Bearer ${token}`,
});
const REQUEST_TIMEOUT_MS = 12000;

export async function handleResponse<T>(response: Response): Promise<T> {
  const text = await response.text();

  if (!response.ok) {
    if (!text) {
      throw new ApiException(
        response.status,
        getDefaultErrorMessage(response.status),
      );
    }

    const data: unknown = JSON.parse(text);
    const error = data as ApiError;

    throw new ApiException(
      error.status,
      error.message,
      error.errors,
      error.timestamp,
    );
  }

  if (!text) {
    return undefined as T;
  }

  const contentType = response.headers.get("Content-Type");

  if (contentType?.includes("application/json")) {
    const data: unknown = JSON.parse(text);
    return data as T;
  }

  return text as T; // Se devuelve el token como texto plano al iniciar sesión
}

function getDefaultErrorMessage(status: number): string {
  switch (status) {
    case 401:
      return "No estás autenticado.";
    case 403:
      return "No tienes permisos para realizar esta acción.";
    case 404:
      return "El recurso solicitado no existe.";
    case 409:
      return "La operación entra en conflicto con el estado actual.";
    case 500:
      return "Se ha producido un error interno del servidor.";
    default:
      return "Se ha producido un error inesperado.";
  }
}

export async function fetchWithTimeout(
  input: RequestInfo | URL,
  init: RequestInit,
): Promise<Response> {
  const controller = new AbortController();
  const timeoutId = setTimeout(() => controller.abort(), REQUEST_TIMEOUT_MS);

  try {
    return await fetch(input, {
      ...init,
      signal: controller.signal,
    });
  } catch (error) {
    if (error instanceof Error && error.name === "AbortError") {
      throw new ApiException(
        408,
        "Tiempo de espera del servidor agotado. Vuelve a intentarlo más tarde.",
      );
    }

    throw error;
  } finally {
    clearTimeout(timeoutId);
  }
}

export function isApiError(err: unknown): err is ApiError {
  if (err == null || err == undefined) return false;
  return typeof err === "object" && "status" in err && "message" in err;
}

export function isFieldError(
  err: unknown,
): err is ApiError & { errors: Record<string, string> } {
  if (!isApiError(err)) return false;
  return err.errors !== undefined && Object.keys(err.errors).length > 0;
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

export function removeEmptyFields<T extends object>(value: T): Partial<T> {
  return Object.fromEntries(
    Object.entries(value).filter(([, fieldValue]) => {
      if (
        fieldValue === null ||
        fieldValue === undefined ||
        fieldValue === ""
      ) {
        return false;
      }

      return !Array.isArray(fieldValue) || fieldValue.length > 0;
    }),
  ) as Partial<T>;
}