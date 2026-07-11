export const MAX_LENGTH = {
  TITLE: 50,
  DESCRIPTION: 280,
};

export const validateSuggestionForm = (
  form: {
    title: string;
    description?: string;
  },
  token?: string | null,
): Partial<Record<string, string>> => {
  const errors: Partial<Record<string, string>> = {};

  if (!token) {
    errors.general = "Debes iniciar sesión para crear una sugerencia";
    return errors;
  }

  if (!form.title.trim()) {
    errors.title = "El título es obligatorio";
  } else if (form.title.trim().length < 3) {
    errors.title = "El título debe tener al menos 3 caracteres";
  } else if (form.title.trim().length > MAX_LENGTH.TITLE) {
    errors.title = `El título no puede exceder los ${MAX_LENGTH.TITLE} caracteres`;
  }

  if (
    form.description &&
    form.description.trim() &&
    form.description.trim().length > MAX_LENGTH.DESCRIPTION
  ) {
    errors.description = `La descripción no puede exceder los ${MAX_LENGTH.DESCRIPTION} caracteres`;
  }

  return errors;
};
