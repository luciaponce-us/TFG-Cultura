import {
  MAX_LENGTH as MAX_LENGTH_ITEM,
  validateItemForm,
} from "@/modules/catalog/validations/item.validations";
import type {
  Platform,
  VideoGameErrors,
  VideoGameRequest,
} from "../types/videogame";
import { isPastOrPresentDate } from "@/modules/core/utils/validations.utils";

export const MAX_LENGTH = {
  ...MAX_LENGTH_ITEM,
  TRAILER_URL: 280,
};

export function validateVideoGameForm(form: VideoGameRequest): VideoGameErrors {
  const base = validateItemForm(form);
  const errors: VideoGameErrors = {
    ...base,
    platform: validatePlatform(form.platform),
    releaseDate: validateReleaseDate(form.releaseDate),
    trailerUrl: validateTrailerUrl(form.trailerUrl),
  };

  return Object.fromEntries(
    Object.entries(errors).filter(([, error]) => error !== undefined),
  );
}

function validatePlatform(value: Platform): string | undefined {
  if (!value) return "La plataforma es obligatoria.";
  return;
}

function validateReleaseDate(value: string): string | undefined {
  if (!value) return "La fecha de lanzamiento es obligatoria.";
  if (!isPastOrPresentDate(value))
    return "La fecha de lanzamiento no puede ser futura.";
  return;
}

function validateTrailerUrl(value: string): string | undefined {
  if (value.length > MAX_LENGTH.TRAILER_URL)
    return `La URL del tráiler no puede superar los ${MAX_LENGTH.TRAILER_URL} caracteres.`;
  return;
}
