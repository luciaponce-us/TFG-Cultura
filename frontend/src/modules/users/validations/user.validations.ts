export const validateName = (value: string): string => {
  if (!value) return "El nombre es obligatorio.";
  if (value.length < 2) return "El nombre debe tener al menos 2 caracteres.";
  if (value.length > 50)
    return "El nombre pueden tener 50 caracteres como máximo.";
  return "";
};

export const validateSurname = (value: string): string => {
  if (!value) return "Los apellidos son obligatorios.";
  if (value.length < 2)
    return "Los apellidos deben tener al menos 2 caracteres.";
  if (value.length > 50)
    return "Los apellidos pueden tener 50 caracteres como máximo.";
  return "";
};

export const validateDni = (value: string): string => {
  if (!value) return "El DNI es obligatorio.";
  const regex = /^\d{8}[A-Za-z]$/;
  if (!regex.test(value)) {
    return "El DNI debe tener 8 números y una letra.";
  }

  const numberPart = value.substring(0, 8);
  const letterPart = value.charAt(8).toUpperCase();

  const letters = "TRWAGMYFPDXBNJZSQVHLCKE";
  const index = Number.parseInt(numberPart, 10) % 23;

  if (letterPart !== letters.charAt(index)) {
    return "La letra del DNI no es válida.";
  }

  return "";
};

export const validateUsername = (value: string): string => {
  if (!value) return "El nombre de usuario es obligatorio.";
  if (value.length < 3)
    return "El nombre de usuario debe tener al menos 3 caracteres.";
  if (value.length > 20)
    return "El nombre de usuario puede tener 20 caracteres como máximo.";
  return "";
};

export const validatePassword = (
  value: string,
  optional: boolean = false,
  withConfirmation: boolean,
  confirmationValue?: string,
): string => {
  if (!value) {
    if (optional) return "";
    return "La contraseña es obligatoria.";
  }
  if (value.length < 8)
    return "La contraseña debe tener al menos 8 caracteres.";
  if (value.length > 64)
    return "La contraseña puede tener 64 caracteres como máximo.";
  if (withConfirmation) {
    if (!confirmationValue || confirmationValue == "")
      return "Confirma la contraseña.";
    if (value != confirmationValue) return "Las contraseñas no coinciden.";
  }
  return "";
};

export const validateEmail = (value: string): string => {
  if (!value) return "El correo electrónico es obligatorio.";
  if (value.length < 5)
    return "El correo electrónico debe tener al menos 5 caracteres.";
  if (value.length > 254)
    return "El correo electrónico puede tener 254 caracteres como máximo.";

  const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
  if (!emailRegex.test(value)) {
    return "El correo electrónico no es válido.";
  }
  return "";
};

export const validatePhone = (value: string): string => {
  if (!value) return "El teléfono es obligatorio.";

  const cleaned = value.replaceAll(/[\s\-()]/g, "");

  const regex = /^(\+\d{1,3}|00\d{1,3})?\d{9}$/;
  if (!regex.test(cleaned)) {
    return "El teléfono no es válido.";
  }
  return "";
};

// LOGIN VALIDATIONS

export const validateUsernameAtLogin = (value: string): string => {
  if (!value) return "El nombre de usuario es obligatorio.";
  if (value.length > 20)
    return "El nombre de usuario puede tener 20 caracteres como máximo.";
  return "";
};

export const validatePasswordAtLogin = (value: string): string => {
  if (!value) return "La contraseña es obligatoria.";
  if (value.length > 64)
    return "La contraseña puede tener 64 caracteres como máximo.";
  return "";
};
