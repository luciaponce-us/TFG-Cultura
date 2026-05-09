export const validateName = (value: string): string => {
  if (!value) return "El nombre es obligatorio";
  if (value.length < 2) return "El nombre debe tener al menos 2 caracteres";
  if (value.length > 50) return "El nombre debe tener menos de 50 caracteres";
  return "";
};

export const validateSurname = (value: string): string => {
  if (!value) return "Los apellidos son obligatorios";
  if (value.length < 2)
    return "Los apellidos deben tener al menos 2 caracteres";
  if (value.length > 50)
    return "Los apellidos deben tener menos de 50 caracteres";
  return "";
};

export const validateDni = (value: string): string => {
  if (!value) return "El DNI es obligatorio";
  const regex = /^\d{8}[A-Za-z]$/;
  if (!regex.test(value)) {
    return "El DNI debe tener 8 números y una letra";
  }

  const numberPart = value.substring(0, 8);
  const letterPart = value.charAt(8).toUpperCase();

  const letters = "TRWAGMYFPDXBNJZSQVHLCKE";
  const index = Number.parseInt(numberPart, 10) % 23;

  if (letterPart !== letters.charAt(index)) {
    return "La letra del DNI no es válida";
  }

  return "";
};
