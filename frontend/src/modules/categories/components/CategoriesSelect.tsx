import type { FiltersGetAllItems, ItemRequest } from "@/modules/catalog/types";
import { CustomSelect } from "@/modules/core/components";
import type { Dispatch, SetStateAction } from "react";
import { useCategories } from "../hooks";
import type { RolSagaRequest } from "@/modules/catalog/types/rolgame";

interface CategoriesSelectProps<
  T extends ItemRequest | RolSagaRequest | FiltersGetAllItems,
> {
  form: T;
  setForm: Dispatch<SetStateAction<T>>;
  onCreateCategory?: () => void;
  error?: string;
  disabled?: boolean;
}

export function CategoriesSelect<
  T extends ItemRequest | RolSagaRequest | FiltersGetAllItems,
>({
  form,
  setForm,
  onCreateCategory,
  error,
  disabled = false,
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
      placeholder="Selecciona las categorías del ítem"
      onValueChange={handleCategoriesChange}
      value={form.categoriesIds || []}
      defaultValue={form.categoriesIds || []}
      loading={isCategoriesLoading}
      error={isCategoriesError ? "Error al cargar las categorías" : error}
      multiple
      onCreate={onCreateCategory}
      onCreateLabel="Crear nueva categoría"
      disabled={disabled}
    />
  );
}
