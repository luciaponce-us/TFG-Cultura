import { ROLES, type Role } from "../types";

export function parsePaymentReceiptUrl(url: string | null | undefined): string {
  if (!url) return "";
  if (url.includes("cloudinary")) {
    return (
      "https://docs.google.com/gview?embedded=true&url=" +
      encodeURIComponent(url)
    );
  }
  return url;
}

/**
 * Extrae el nombre de archivo de una URL, eliminando cualquier carácter de control y caracteres no permitidos en nombres de archivo.
 * @param url 
 * @returns 
 */
export function parseUrlFilename(url: string | null | undefined): string {
  if (!url) return "";

  const trimmed = url.trim();

  const cleaned = trimmed.split("?")[0].split("#")[0];

  const parts = cleaned.split("/").filter(Boolean);
  const filename = parts.at(-1) ?? "";

  const controlChars = Array.from({ length: 32 }, (_, i) =>
    String.fromCodePoint(i)
  ).join("");

  const controlRegex = new RegExp(`[${controlChars}]`, "g");

 const sanitizedFilename = filename.replace(controlRegex, "")
    .replace(/[<>:"|?*]/g, "")
    .trim();

  return sanitizedFilename;
}

export function parseRole(role: Role): string {
  return role.charAt(0) + role.slice(1).toLowerCase();
}

export const roleOptions = ROLES.map((role) => ({
  value: role,
  label: parseRole(role),
}));

export const activeOptions = [
  { value: "true", label: "Activo" },
  { value: "false", label: "Inactivo" },
];