import type { MovieRequest, MovieErrors } from "../types/movie";
import { validateItemForm } from "./item.validations";

export const MAX_LENGTH = {
  TRAILER_URL: 500,
  SAGA_NAME: 50,
};

export function validateMovieForm(
  form: MovieRequest,
  token?: string | null,
): MovieErrors {
  const base = validateItemForm(form, token);

  return {
    ...base,
    format: form.format ? undefined : "El formato es obligatorio.",
    numberOfDiscs: validateNumberOfDiscs(form.numberOfDiscs),
    releaseDate: validateReleaseDate(form.releaseDate),
    trailerUrl: validateTrailerUrl(form.trailerUrl),
    sagaName: validateSagaName(form.sagaName),
  };
}

function validateNumberOfDiscs(value: number): string | undefined {
  if (!Number.isInteger(value) || value < 1) {
    return "El número de discos debe ser al menos 1.";
  }
  return undefined;
}

function validateReleaseDate(value?: string): string | undefined {
  if (!value) return undefined;
  return Number.isNaN(new Date(value).getTime())
    ? "La fecha de estreno no es válida."
    : undefined;
}

function validateTrailerUrl(value?: string): string | undefined {
  if (!value) return undefined;
  if (value.length > MAX_LENGTH.TRAILER_URL) {
    return `El tráiler no puede superar los ${MAX_LENGTH.TRAILER_URL} caracteres.`;
  }
  return /^https:\/\/www\.youtube\.com\/embed\/[a-zA-Z0-9_-]+$/.test(value)
    ? undefined
    : "La URL del tráiler no es válida. Debe tener el formato: https://www.youtube.com/embed/VIDEO_ID";
}

function validateSagaName(value?: string): string | undefined {
  if (value && value.trim().length > MAX_LENGTH.SAGA_NAME) {
    return `La saga no puede superar los ${MAX_LENGTH.SAGA_NAME} caracteres.`;
  }
  return undefined;
}