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
import { removeEmptyFields } from "@/modules/core/utils/utils";
import type { Dispatch, SetStateAction } from "react";
import { toaster } from "@/modules/core/components/toaster/toaster";

export const MAX_LENGTH = {
  ...MAX_LENGTH_ITEM,
  TRAILER_URL: 280,
};

export function validateVideoGameForm(
  form: VideoGameRequest,
  setErrors: Dispatch<SetStateAction<VideoGameErrors>>,
): void {
  const base = validateItemForm(form);
  let errors: VideoGameErrors = {
    ...base,
    platform: validatePlatform(form.platform),
    releaseDate: validateReleaseDate(form.releaseDate),
    trailerUrl: validateTrailerUrl(form.trailerUrl),
  };

  errors = removeEmptyFields(errors);
  if (Object.keys(errors).length > 0) {
    setErrors(errors);
    toaster.create({
      title: "Error al crear videojuego",
      description:
        "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
      type: "error",
    });
  }
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
