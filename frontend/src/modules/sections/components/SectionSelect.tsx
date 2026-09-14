import { CustomSelect } from "@/modules/core/components";
import { useSections } from "../hooks";
import { useEffect } from "react";
import type { ItemErrors, ItemRequest } from "@/modules/catalog/types";
import type { Dispatch, SetStateAction } from "react";
import type { RolSagaErrors, RolSagaRequest } from "@/modules/catalog/types/rolgame";

interface SectionSelectProps<T extends ItemRequest | RolSagaRequest, E extends ItemErrors | RolSagaErrors> {
  form: T;
  setForm: Dispatch<SetStateAction<T>>;
  errors: E;
  setErrors: Dispatch<SetStateAction<E>>;
  defaultValueText?: string;
}

export function SectionSelect<T extends ItemRequest | RolSagaRequest, E extends ItemErrors | RolSagaErrors>({
  form,
  setForm,
  errors,
  setErrors,
  defaultValueText,
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
  const defaultSection = defaultValueText
    ? sectionsOptions.find((option) =>
        option.label.toLocaleLowerCase().includes(defaultValueText.toLocaleLowerCase()),
      )
    : undefined;

  useEffect(() => {
    if (!form.sectionId && defaultSection) {
      setForm((previous) => ({ ...previous, sectionId: defaultSection.value }));
    }
  }, [defaultSection, form.sectionId, setForm]);

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
      defaultValue={defaultSection ? [defaultSection.value] : undefined}
      loading={isSectionsLoading}
      error={
        isSectionsError
          ? "Error al cargar las secciones"
          : (errors.sectionId ?? "")
      }
    />
  );
}
