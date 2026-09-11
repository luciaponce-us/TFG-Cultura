import type { ItemRequest } from "@/modules/catalog/types";
import { CustomSelect } from "@/modules/core/components";
import type { Dispatch, SetStateAction } from "react";
import { useCategories } from "../hooks";

interface CategoriesSelectProps<T extends ItemRequest> {
  form: T;
  setForm: Dispatch<SetStateAction<T>>;
  onCreateCategory?: () => void;
  error?: string;
}

export function CategoriesSelect<T extends ItemRequest>({
  form,
  setForm,
  onCreateCategory,
  error,
}: CategoriesSelectProps<T>) {
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
      error={isCategoriesError ? "Error al cargar las categorías" : error}
      multiple
      onCreate={onCreateCategory}
      onCreateLabel="Crear nueva categoría"
    />
  );
}
