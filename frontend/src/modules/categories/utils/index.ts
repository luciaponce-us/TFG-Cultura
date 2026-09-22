import type { Category, CategoryCreateRequest } from "../types";

export function toCategoryRequest(category: Category): CategoryCreateRequest {
  return {
    name: category.name,
    color: category.color,
  };
}

export function isLightColor(color: string): boolean {
  const hex = color.replace("#", "");

  const r = parseInt(hex.substring(0, 2), 16) / 255;
  const g = parseInt(hex.substring(2, 4), 16) / 255;
  const b = parseInt(hex.substring(4, 6), 16) / 255;

  const luminance = 0.299 * r + 0.587 * g + 0.114 * b;

  return luminance > 0.7;
}