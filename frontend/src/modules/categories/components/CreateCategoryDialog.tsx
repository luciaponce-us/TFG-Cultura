import { useState, type Dispatch, type SetStateAction } from "react";
import {
  defaultCategory,
  type Category,
  type CategoryCreateRequest,
  type CategoryFormErrors,
} from "../types";
import {
  CustomColorPicker,
  CustomInput,
  FormDialog,
} from "@/modules/core/components";
import { useCreateCategory } from "../hooks";
import { handleChange } from "@/modules/core/utils/utils";

interface CreateCategoryDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: Dispatch<SetStateAction<boolean>>;
  readonly onCategoryCreated: (category: Category) => void;
}

export function CreateCategoryDialog({
  isOpen,
  setIsOpen,
  onCategoryCreated,
}: CreateCategoryDialogProps) {
  const [category, setCategory] =
    useState<CategoryCreateRequest>(defaultCategory);
  const [errors, setErrors] = useState<CategoryFormErrors>({});
  const { mutateAsync: createCategory } = useCreateCategory(
    setErrors,
    setIsOpen,
  );

  async function handleSubmit() {
    if (!category) {
      return;
    }
    const createdCategory: Category | undefined =
      await createCategory(category);
    if (!createdCategory) {
      return;
    }
    onCategoryCreated(createdCategory);
    setIsOpen(false);
  }

  return (
    <FormDialog
      isOpen={isOpen}
      setIsOpen={setIsOpen}
      title="Crear categoría"
      handleSubmit={handleSubmit}
      submitButtonText="Crear"
    >
      <CustomInput
        label="Nombre"
        name="name"
        placeholder="Introduce el nombre de la categoría..."
        required
        error={errors.name}
        onChange={(e) => handleChange(e, category, setErrors, setCategory)}
      />

      <CustomColorPicker
        required
        onChange={(e) => handleChange(e, category, setErrors, setCategory)}
        defaultValue={category?.color || "#000000"}
        error={errors.color}
      />
    </FormDialog>
  );
}
