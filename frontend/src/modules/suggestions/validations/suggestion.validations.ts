export const validateSuggestionForm = (form: {
  title: string;
  description?: string;
}): Partial<Record<keyof typeof form, string>> => {
  const errors: Partial<Record<keyof typeof form, string>> = {};

  if (!form.title.trim()) {
    errors.title = "El título es obligatorio";
  } else if (form.title.trim().length < 3) {
    errors.title = "El título debe tener al menos 3 caracteres";
  } else if (form.title.trim().length > 50) {
    errors.title = "El título no puede exceder los 50 caracteres";
  }

  if (form.description && form.description.trim() && form.description.trim().length > 280) {
    errors.description = "La descripción no puede exceder los 280 caracteres";
  }

  return errors;
}