import type { SeriesErrors, SeriesRequest } from "../types/series";
import { validateItemForm } from "./item.validations";

export const MAX_LENGTH = {
  TRAILER_URL: 500,
};

export function validateSeriesForm(
  form: SeriesRequest,
  token?: string | null,
): SeriesErrors {
  const base = validateItemForm(form, token);

  return {
    ...base,
    format: form.format ? undefined : "El formato es obligatorio.",
    numberOfDiscs:
      Number.isInteger(form.numberOfDiscs) && form.numberOfDiscs >= 1
        ? undefined
        : "El número de discos debe ser al menos 1.",
    releaseDate: validateReleaseDate(form.releaseDate),
    purchasedAt: validatePurchasedAt(
      form.purchasedAt,
      form.releaseDate,
      base.purchasedAt,
    ),
    numberOfSeasons: validateNumberOfSeasons(form.numberOfSeasons),
    status: form.status ? undefined : "El estado de la serie es obligatorio.",
    seasons: validateSeasons(form),
  };
}

function validateReleaseDate(value: string): string | undefined {
  if (!value) return "La fecha de estreno es obligatoria.";
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "La fecha de estreno no es válida.";
  return date < new Date() ? undefined : "La fecha de estreno debe ser pasada.";
}

function validateNumberOfSeasons(value: number): string | undefined {
  if (!Number.isInteger(value) || value < 1 || value > 1000) {
    return "El número de temporadas debe estar entre 1 y 1000.";
  }
  return undefined;
}

function validatePurchasedAt(
  purchasedAt: string,
  releaseDate: string,
  baseError?: string,
): string | undefined {
  if (baseError || !purchasedAt || !releaseDate) return baseError;

  if (new Date(purchasedAt) < new Date(releaseDate)) {
    return "La fecha de compra no puede ser anterior a la fecha de estreno.";
  }

  return undefined;
}

function validateSeasons(form: SeriesRequest): string | undefined {
  if (!form.seasons.length) return "Debe existir al menos una temporada.";

  for (const season of form.seasons) {
    if (
      !Number.isInteger(season.seasonNumber) ||
      season.seasonNumber < 0 ||
      season.seasonNumber > 1000
    ) {
      return "El número de temporada debe estar entre 0 y 1000.";
    }
    if (season.seasonNumber > form.numberOfSeasons) {
      return "El número de temporada no puede superar el total de temporadas de la serie.";
    }
    if (
      season.seasonPart !== undefined &&
      (!Number.isInteger(season.seasonPart) ||
        season.seasonPart < 0 ||
        season.seasonPart > 10)
    ) {
      return "La parte de temporada debe estar entre 0 y 10.";
    }
    if (
      season.trailerUrl &&
      (season.trailerUrl.length > MAX_LENGTH.TRAILER_URL ||
        !/^https:\/\/www\.youtube\.com\/embed\/[a-zA-Z0-9_-]+$/.test(
          season.trailerUrl,
        ))
    ) {
      return "La URL del tráiler no es válida.";
    }
  }

  return undefined;
}
