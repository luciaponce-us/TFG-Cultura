export const API_BASE_URL = import.meta.env.VITE_API_BASE_URL;
import type { ApiError } from "../types";

export const jsonHeaders = { "Content-Type": "application/json" };
const REQUEST_TIMEOUT_MS = 12000;

export async function handleResponse<T>(res: Response): Promise<T> {
  const contentType = res.headers.get("content-type") ?? "";

  if (!res.ok) {
    let message = `Error ${res.status}`;
    let data: unknown = null;

    try {
      if (contentType.includes("application/json")) {
        data = await res.json();

        if (typeof data === "object" && data !== null) {
          const d = data as Partial<ApiError>;
          message = d.message || d.error || message;
        }
      } else {
        const text = await res.text();
        message = text || message;
      }
    } catch {
      // ignore parsing errors
    }

    const apiError: ApiError = {
      timestamp: new Date().toISOString(),
      status: res.status,
      error: "Request failed",
      message,
    };

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
      const apiError: ApiError = {
      timestamp: new Date().toISOString(),
      status: 500,
      error: "Request failed",
      message: "Tiempo de espera del servidor agotado. Vuelve a intentarlo más tarde.",
    };
    throw apiError
    }
    throw error;
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
