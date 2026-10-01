import { useState, type Dispatch, type SetStateAction } from "react";

import {
  defaultCategory,
  type Category,
  type CategoryCreateRequest,
} from "../types";
import { toCategoryRequest } from "../utils";

export function useCategoryForm(
  categoryId: string | undefined,
  categoryToUpdate?: Category,
  isLoading = false,
) {
  const [formOverride, setFormOverride] = useState<{
    categoryId: string | undefined;
    value: CategoryCreateRequest;
  }>();

  const loadedForm = categoryToUpdate
    ? toCategoryRequest(categoryToUpdate)
    : defaultCategory;

  const form =
    formOverride !== undefined && formOverride.categoryId === categoryId
      ? formOverride.value
      : loadedForm;

  const setForm: Dispatch<SetStateAction<CategoryCreateRequest>> = (
    nextForm,
  ) => {
    if (categoryId && isLoading) {
      return; // Esperando a que se cargue la categoría a editar
    }
    setFormOverride((currentOverride) => {
      const currentForm =
        currentOverride !== undefined &&
        currentOverride.categoryId === categoryId
          ? currentOverride.value
          : loadedForm;

      return {
        categoryId,
        value:
          typeof nextForm === "function" ? nextForm(currentForm) : nextForm,
      };
    });
  };

  return {
    form,
    setForm,
  };
}
