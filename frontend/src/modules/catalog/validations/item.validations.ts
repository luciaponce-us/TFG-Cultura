import type { ItemCreateRequest, ItemCreateRequestErrors } from "../types";

export const MAX_LENGTH = {
  NAME: 50,
  DESCRIPTION: 280,
  COMMENTS: 280,
};

export function validateItemForm(
  form: ItemCreateRequest,
  token?: string | null,
): ItemCreateRequestErrors {
  return {
    name: validateName(form.name),
    description: validateDescription(form.description),
    condition: validateCondition(form.condition),
    comments: validateComments(form.comments),
    loanAvailable: validateLoanAvailable(form.loanAvailable),
    publicated: validatePublished(form.publicated),
    purchasedAt: validatePurchasedAt(form.purchasedAt),
    price: validatePrice(form.price),
    copies: validateCopies(form.copies),
    availableCopies: validateAvailableCopies(form.availableCopies),
    sectionId: validateSectionId(form.sectionId),
    categoriesIds: validateCategoriesIds(form.categoriesIds),
    general: token ? undefined : "Necesitas estar logueado para crear un item.",
  };
}

function validateName(name: string): string | undefined {
  if (!name || name.trim() === "") return "El nombre es obligatorio.";
  if (name.trim().length < 3 || name.trim().length > MAX_LENGTH.NAME) {
    return "El nombre debe tener entre 3 y " + MAX_LENGTH.NAME + " caracteres.";
  }
  return undefined;
}

function validateDescription(description: string): string | undefined {
  if (description && description.length > MAX_LENGTH.DESCRIPTION) {
    return (
      "La descripción no puede tener más de " +
      MAX_LENGTH.DESCRIPTION +
      " caracteres."
    );
  }
  return undefined;
}

function validateCondition(condition: string): string | undefined {
  return condition ? undefined : "El estado es obligatorio.";
}

function validateComments(comments: string): string | undefined {
  if (comments && comments.length > MAX_LENGTH.COMMENTS) {
    return (
      "Los comentarios no pueden tener más de " +
      MAX_LENGTH.COMMENTS +
      " caracteres."
    );
  }
  return undefined;
}

function validateLoanAvailable(value: boolean): string | undefined {
  return value === undefined ? "La disponibilidad es obligatoria." : undefined;
}

function validatePublished(value: boolean): string | undefined {
  return value === undefined ? "La visibilidad es obligatoria." : undefined;
}

function validatePurchasedAt(value: string): string | undefined {
  if (!value) return undefined;
  const date = new Date(value);
  if (Number.isNaN(date.getTime())) return "La fecha de compra no es válida.";
  if (date > new Date()) return "La fecha de compra no puede ser futura.";
  return undefined;
}

function validatePrice(value: number): string | undefined {
  if (value === undefined || value === null) return "El precio es obligatorio.";
  if (value < 0) return "El precio debe ser mayor o igual que 0.";
  if (!Number.isFinite(value)) return "El precio no es válido.";
  return undefined;
}

function validateCopies(value: number): string | undefined {
  if (value === undefined || value === null)
    return "Las copias son obligatorias.";
  if (value < 1) return "Las copias deben ser al menos 1.";
  return undefined;
}

function validateAvailableCopies(value: number): string | undefined {
  if (value === undefined || value === null) {
    return "Las copias disponibles son obligatorias.";
  }
  if (value < 0) return "Las copias disponibles no pueden ser negativas.";
  return undefined;
}

function validateSectionId(value: string): string | undefined {
  if (!value || value.trim() === "") return "La sección es obligatoria.";
  return undefined;
}

function validateCategoriesIds(value: string[]): string | undefined {
  if (value && value.some((id) => !id || id.trim() === "")) {
    return "Hay categorías no válidas.";
  }
  return undefined;
}
