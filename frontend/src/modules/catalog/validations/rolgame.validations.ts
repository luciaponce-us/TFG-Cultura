import {
  MAX_LENGTH as MAX_LENGTH_ITEM,
  validateItemForm,
} from "@/modules/catalog/validations/item.validations";
import type {
  RolBookType,
  RolGameErrors,
  RolGameRequest,
} from "../types/rolgame";
import { removeEmptyFields } from "@/modules/core/utils/utils";
import type { Dispatch, SetStateAction } from "react";
import { toaster } from "@/modules/core/components/toaster/toaster";

export const MAX_LENGTH = {
  ...MAX_LENGTH_ITEM,
};

export function validateRolGameForm(
  form: RolGameRequest,
  setErrors: Dispatch<SetStateAction<RolGameErrors>>,
): void {
  const base = validateItemForm(form);
  let errors: RolGameErrors = {
    ...base,
    type: validateType(form.type),
    sagaId: validateSagaId(form.sagaId),
  };

  errors = removeEmptyFields(errors);

  if (Object.values(errors).some(Boolean)) {
    setErrors(errors);
    toaster.create({
      title: "Error al crear juego de rol",
      description:
        "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
      type: "error",
    });
  }
}

function validateType(value: RolBookType): string | undefined {
  if (!value) return "El tipo de juego de rol es obligatorio.";
  return;
}

function validateSagaId(value: string): string | undefined {
  if (!value) return "La saga del juego de rol es obligatoria.";
  return;
}
