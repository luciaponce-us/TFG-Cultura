import { CustomSelect } from "@/modules/core/components";
import { useSections } from "../hooks";
import { handleSelectChange } from "@/modules/core/utils/utils";
import type {
  BookCreateRequest,
  BookCreateRequestErrors,
} from "@/modules/catalog/types";
import type { Dispatch, SetStateAction } from "react";

interface SectionSelectProps {
  form: BookCreateRequest;
  setForm: Dispatch<SetStateAction<BookCreateRequest>>;
  errors: BookCreateRequestErrors;
  setErrors: Dispatch<SetStateAction<BookCreateRequestErrors>>;
}

export function SectionSelect({
  form,
  setForm,
  errors,
  setErrors,
}: SectionSelectProps) {
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
  const handleSectionChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "sectionId", form, setErrors, setForm);

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
