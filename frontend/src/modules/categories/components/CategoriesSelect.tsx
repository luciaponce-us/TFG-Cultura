import type { BookCreateRequest } from "@/modules/catalog/types";
import { CustomSelect } from "@/modules/core/components";
import type { Dispatch, SetStateAction } from "react";
import { useCategories } from "../hooks";

interface CategoriesSelectProps {
  form: BookCreateRequest;
  setForm: Dispatch<SetStateAction<BookCreateRequest>>;
  onCreateCategory?: () => void;
}

export function CategoriesSelect({ form, setForm, onCreateCategory }: CategoriesSelectProps) {
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
      onCreate={onCreateCategory}
      onCreateLabel="Crear nueva categoría"
    />
  );
}
