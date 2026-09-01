import type { BookCreateRequest, BookCreateRequestErrors } from "../types";
import { validateItemForm } from "./item.validations";

export const MAX_LENGTH = {
  AUTHOR: 100,
  ISBN: 13,
  SAGA_NAME: 50,
};

export function validateBookForm(
  form: BookCreateRequest,
  token?: string | null,
): BookCreateRequestErrors {
  const base = validateItemForm(form, token);

  return {
    ...base,
    author: validateAuthor(form.author),
    isbn: validateIsbn(form.isbn),
    type: validateType(form.type),
    sagaName: validateSagaName(form.sagaName),
  };
}

function validateAuthor(value: string): string | undefined {
  if (!value || value.trim() === "") return "El autor es obligatorio.";
  if (value.trim().length > MAX_LENGTH.AUTHOR) {
    return (
      "El autor no puede superar los " + MAX_LENGTH.AUTHOR + " caracteres."
    );
  }
  return undefined;
}

function validateIsbn(value: string): string | undefined {
  if (!value || value.trim() === "") return "El ISBN es obligatorio.";
  const clean = value.replace(/[-\s]/g, "");
  if (!/^(\d{10}|\d{13})$/.test(clean) && !/^(\d{9}[\dXx])$/.test(clean)) {
    return "El ISBN no es válido.";
  }
  return undefined;
}

function validateType(value: string): string | undefined {
  if (!value) return "El tipo de libro es obligatorio.";
  return undefined;
}

function validateSagaName(value: string): string | undefined {
  if (value && value.trim().length > MAX_LENGTH.SAGA_NAME) {
    return (
      "La saga no puede superar los " + MAX_LENGTH.SAGA_NAME + " caracteres."
    );
  }
  return undefined;
}
