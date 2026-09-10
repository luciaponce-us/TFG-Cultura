export interface Category {
  id: string;
  name: string;
  color: string; // Hexadecimal color code
}

export interface CategoryCreateRequest {
  name: string;
  color: string; // Hexadecimal color code
}

export const defaultCategory: CategoryCreateRequest = {
  name: "",
  color: "#000000", // Default to black
};

export interface CategoryFormErrors {
  name?: string;
  color?: string;
}
