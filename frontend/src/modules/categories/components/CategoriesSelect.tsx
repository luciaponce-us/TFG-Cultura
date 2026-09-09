import type { BookCreateRequest } from "@/modules/catalog/types";
import { CustomSelect } from "@/modules/core/components";
import type { Dispatch, SetStateAction } from "react";
import { useCategories } from "../hooks";

interface CategoriesSelectProps {
  form: BookCreateRequest;
  setForm: Dispatch<SetStateAction<BookCreateRequest>>;
}

// FIXME: Add onCreateCategory functionality to allow creating new categories from the select component

export function CategoriesSelect({
  form,
  setForm
}: CategoriesSelectProps) {
  const {
    data: categories,
    isLoading: isCategoriesLoading,
    isError: isCategoriesError,
  } = useCategories();
  const categoriesOptions: { value: string; label: string }[] =
    categories?.map((category) => ({
      value: category.id,
      label: category.name,
    })) || [];

  const handleCategoriesChange = ({ value }: { value: string[] }) =>
    setForm((prev) => ({ ...prev, categoriesIds: value }));
  return (
    <CustomSelect
      label="Categorías"
      name="categories"
      options={categoriesOptions}
      placeholder="Selecciona las categorías del libro"
      onValueChange={handleCategoriesChange}
      value={form.categoriesIds || []}
      loading={isCategoriesLoading}
      error={isCategoriesError ? "Error al cargar las categorías" : null}
      multiple
    />
  );
}
