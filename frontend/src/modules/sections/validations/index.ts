import type { Dispatch, SetStateAction } from "react";
import type { SectionErrors, SectionRequest } from "../types";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { removeEmptyFields } from "@/modules/core/utils/utils";

export const MAX_LENGTH = {
  NAME: 50,
};

export function validateSectionForm(
  form: SectionRequest,
  setErrors: Dispatch<SetStateAction<SectionErrors>>,
  isUpdate: boolean = false,
): boolean {
  let errors: SectionErrors = {
    name: validateName(form.name),
    managersUsernames: validateManagers(form.managersUsernames),
  };

  errors = removeEmptyFields(errors);

  const hasErrors = Object.values(errors).some(Boolean);

  if (hasErrors) {
    setErrors(errors);
    toaster.create({
      title: `Error al ${isUpdate ? "editar" : "crear"} juego de mesa`,
      description:
        "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
      type: "error",
    });
  }
  return !hasErrors;
}

function validateName(name: string): string | undefined {
  if (!name || name.trim() === "") {
    return "El nombre es obligatorio.";
  }
  if (name.length > MAX_LENGTH.NAME) {
    return `El nombre no puede tener más de ${MAX_LENGTH.NAME} caracteres.`;
  }
  if (name.length < 3) {
    return "El nombre debe tener al menos 3 caracteres.";
  }
  return undefined;
}

function validateManagers(managers: string[]): string | undefined {
  if (!managers || managers.length === 0) {
    return "Debe haber al menos un encargado.";
  }
  return undefined;
}
