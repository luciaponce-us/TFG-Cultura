import type { Role, UserProfileUpdateRequest, UserUpdateRequest } from "../types";

export const MAX_LENGTH = {
  NAME: 50,
  SURNAME: 50,
  DNI: 9,
  USERNAME: 20,
  PASSWORD: 64,
  EMAIL: 254,
  PHONE: 15,
};

export const validateName = (value: string): string => {
  if (!value) return "El nombre es obligatorio.";
  if (value.length < 2) return "El nombre debe tener al menos 2 caracteres.";
  if (value.length > MAX_LENGTH.NAME)
    return `El nombre puede tener ${MAX_LENGTH.NAME} caracteres como máximo.`;
  return "";
};

export const validateSurname = (value: string): string => {
  if (!value) return "Los apellidos son obligatorios.";
  if (value.length < 2)
    return "Los apellidos deben tener al menos 2 caracteres.";
  if (value.length > MAX_LENGTH.SURNAME)
    return `Los apellidos pueden tener ${MAX_LENGTH.SURNAME} caracteres como máximo.`;
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
  if (value.length > MAX_LENGTH.USERNAME)
    return `El nombre de usuario puede tener ${MAX_LENGTH.USERNAME} caracteres como máximo.`;
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
  if (value.length > MAX_LENGTH.PASSWORD)
    return `La contraseña puede tener ${MAX_LENGTH.PASSWORD} caracteres como máximo.`;
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
  if (value.length > MAX_LENGTH.EMAIL)
    return `El correo electrónico puede tener ${MAX_LENGTH.EMAIL} caracteres como máximo.`;

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

function isInferiorRole(userRole: string, newRole: string): boolean {
  switch (userRole) {
            case "COORDINADOR":
                return true;
            case "SECRETARIO":
                return newRole == "ENCARGADO" || newRole == "COLABORADOR" || newRole == "SOCIO";
            case "ENCARGADO":
                return newRole == "COLABORADOR" || newRole == "SOCIO";
            case "COLABORADOR":
                return newRole == "SOCIO";
            default:
                return false;
        }
}

function validateRole(loggedUserRole: Role | undefined, value: string): string {
  if (!value) return "";
  if (!loggedUserRole) return "No tienes permisos para asignar un rol.";
  if(!isInferiorRole(loggedUserRole, value)) {
    return "No puedes asignar un rol igual o superior al tuyo.";
  }
  return "";
}

// LOGIN VALIDATIONS

export const validateUsernameAtLogin = (value: string): string => {
  if (!value) return "El nombre de usuario es obligatorio.";
  if (value.length > 20)
    return "El nombre de usuario puede tener 20 caracteres como máximo.";
  return "";
};

export const validatePasswordAtLogin = (value: string | undefined): string => {
  if (!value) return "La contraseña es obligatoria.";
  if (value.length > 64)
    return "La contraseña puede tener 64 caracteres como máximo.";
  return "";
};

export function validateUserUpdateForm(
  loggedUserRole: Role | undefined,
  form: UserUpdateRequest,
): Record<string, string> {
  return {
    username: validateUsername(form.username),
    password: validatePassword(form.password ?? "", true, false),
    name: validateName(form.name),
    surname: validateSurname(form.surname),
    dni: validateDni(form.dni),
    phone: validatePhone(form.phone),
    email: validateEmail(form.email),
    role: validateRole(loggedUserRole, form.role),
    general: "",
  };
}

export function validateUserProfileUpdateForm(
  form: UserProfileUpdateRequest,
): Record<string, string> {
  return {
    username: validateUsername(form.username || ""),
    password: validatePassword(form.password || "", true, false),
    name: validateName(form.name || ""),
    surname: validateSurname(form.surname || ""),
    phone: validatePhone(form.phone || ""),
    email: validateEmail(form.email || ""),
    general: "",
  };
}

export function validateUserLoginForm(
  form: Pick<UserUpdateRequest, "username" | "password">,
): Record<string, string> {
  return {
    username: validateUsernameAtLogin(form.username),
    password: validatePasswordAtLogin(form.password),
    general: "",
  };
}
