import { useState, type Dispatch, type SetStateAction } from "react";
import {
  defaultCategory,
  type Category,
  type CategoryFormErrors,
} from "../types";
import {
  CustomColorPicker,
  CustomInput,
  FormDialog,
} from "@/modules/core/components";
import { useCategory, useCreateCategory, useUpdateCategory } from "../hooks";
import { handleChange } from "@/modules/core/utils/utils";
import { useCategoryForm } from "../hooks/useCategoryForm";

interface CreateCategoryDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: Dispatch<SetStateAction<boolean>>;
  readonly onCategoryCreated?: (category: Category) => void;
  readonly categoryId?: string;
}

export function CreateCategoryDialog({
  isOpen,
  setIsOpen,
  onCategoryCreated,
  categoryId,
}: CreateCategoryDialogProps) {
  const { data: categoryToEdit, isLoading: isCategoryToEditLoading } =
    useCategory(categoryId);
  const { form, setForm } = useCategoryForm(
    categoryId,
    categoryToEdit,
    isCategoryToEditLoading,
  );
  const [errors, setErrors] = useState<CategoryFormErrors>({});
  const { mutateAsync: createCategory } = useCreateCategory(
    setErrors,
    setIsOpen,
  );
  const { mutateAsync: updateCategory } = useUpdateCategory(
    categoryId,
    form,
    setErrors,
    setIsOpen,
    () => {
      setForm(defaultCategory);
      setErrors({});
    },
  );

  async function handleSubmit() {
    if (categoryId) {
      await updateCategory();
      return;
    } else {
      const createdCategory: Category | undefined = await createCategory(form);
      if (!createdCategory) {
        return;
      }
      onCategoryCreated?.(createdCategory);
    }
  }

  if (categoryId && isCategoryToEditLoading) {
    return null; // Esperando a que se cargue la categoría a editar
  }

  return (
    <FormDialog
      isOpen={isOpen}
      setIsOpen={setIsOpen}
      title={categoryToEdit ? "Editando categoría" : "Crear categoría"}
      handleSubmit={handleSubmit}
      submitButtonText={categoryId ? "Guardar" : "Crear"}
    >
      <CustomInput
        label="Nombre"
        name="name"
        placeholder="Introduce el nombre de la categoría..."
        required
        error={errors.name}
        value={form.name}
        onChange={(e) => handleChange(e, form, setErrors, setForm)}
      />

      <CustomColorPicker
        required
        onChange={(e) => handleChange(e, form, setErrors, setForm)}
        defaultValue={form?.color || "#000000"}
        error={errors.color}
      />
    </FormDialog>
  );
}
