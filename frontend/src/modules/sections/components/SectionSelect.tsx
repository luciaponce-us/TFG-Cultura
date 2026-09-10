import { CustomSelect } from "@/modules/core/components";
import { useSections } from "../hooks";
import type { ItemErrors, ItemRequest } from "@/modules/catalog/types";
import type { Dispatch, SetStateAction } from "react";

interface SectionSelectProps<T extends ItemRequest, E extends ItemErrors> {
  form: T;
  setForm: Dispatch<SetStateAction<T>>;
  errors: E;
  setErrors: Dispatch<SetStateAction<E>>;
}

export function SectionSelect<T extends ItemRequest, E extends ItemErrors>({
  form,
  setForm,
  errors,
  setErrors,
}: SectionSelectProps<T, E>) {
  const {
    data: sections,
    isLoading: isSectionsLoading,
    isError: isSectionsError,
  } = useSections();

  const sectionsOptions: { value: string; label: string }[] =
    sections?.map((section) => ({
      value: section.id,
      label: section.name,
    })) || [];
  const handleSectionChange = ({ value }: { value: string[] }) => {
    setErrors({} as E);
    setForm((previous) => ({ ...previous, sectionId: value[0] ?? "" }));
  };

  return (
    <CustomSelect
      label="Sección"
      name="section"
      options={sectionsOptions}
      placeholder="Selecciona la sección a la que pertenece el libro"
      required
      onValueChange={handleSectionChange}
      value={form.sectionId ? [form.sectionId] : []}
      loading={isSectionsLoading}
      error={
        isSectionsError
          ? "Error al cargar las secciones"
          : (errors.sectionId ?? "")
      }
    />
  );
}
