import { useState } from "react";
import type { BookCreateRequest, BookCreateRequestErrors } from "../types";
import {
  BOOK_TYPES_OPTIONS,
  INITIAL_BOOK,
  INITIAL_BOOK_ERRORS,
  ITEM_CONDITIONS_OPTIONS,
} from "../types";
import { useCreateBook, useSagas } from "../hooks";
import { handleChange, handleSelectChange } from "@/modules/core/utils/utils";
import { Heading, Separator } from "@chakra-ui/react";
import {
  CustomInput,
  CustomSelect,
  CustomSwitch,
  CustomNumberInput,
  CustomDateInput,
  FormDialog,
} from "@/modules/core/components";
import {
  MAX_LENGTH as MAX_LENGTH_BOOK,
  validateBookForm,
} from "../validations/book.validations";
import { MAX_LENGTH as MAX_LENGTH_ITEM } from "../validations/item.validations";
import { CreateSagaDialog } from "./";
import { useSections } from "@/modules/sections/hooks";

export function CreateBookDialog({
  isOpen,
  setIsOpen,
  token,
}: {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  token?: string | null;
}) {
  const [form, setForm] = useState<BookCreateRequest>(INITIAL_BOOK);
  const { mutateAsync: createBook } = useCreateBook();
  const [errors, setErrors] =
    useState<BookCreateRequestErrors>(INITIAL_BOOK_ERRORS);

  const {
    data: sagas,
    isLoading: isSagasLoading,
    isError: isSagasError,
  } = useSagas();

  const sagasOptions: { value: string; label: string }[] =
    sagas?.map((saga) => ({
      value: saga.name,
      label: saga.name,
    })) || [];

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

  const [sagaDialogOpen, setSagaDialogOpen] = useState(false);

  const handleTypeChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "type", form, setErrors, setForm);

  const handleConditionChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "condition", form, setErrors, setForm);

  const handleSagaChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "sagaName", form, setErrors, setForm);

  const handleSectionChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "sectionId", form, setErrors, setForm);

  async function handleSubmit() {
    const errors = validateBookForm(form, token);
    setErrors(errors);
    if (Object.keys(errors).length > 0) {
      return;
    }
    await createBook(form);
    setIsOpen(false);
  }

  return (
    <>
      <FormDialog
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        title="Crear libro"
        handleSubmit={handleSubmit}
        submitButtonText="Crear"
      >
        <CustomInput
          label="Título"
          name="title"
          placeholder="Introduce el título..."
          required
          error={errors.name ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH_ITEM.NAME}
        />
        <CustomInput
          label="Autor"
          name="author"
          placeholder="Introduce el nombre del autor..."
          required
          error={errors.author ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH_BOOK.AUTHOR}
        />
        <CustomSelect
          label="Saga"
          name="saga"
          options={sagasOptions}
          placeholder="Selecciona la saga a la que pertenece el libro"
          onValueChange={handleSagaChange}
          value={form.sagaName ? [form.sagaName] : []}
          loading={isSagasLoading}
          error={isSagasError ? "Error al cargar las sagas" : null}
          onCreate={() => setSagaDialogOpen(true)}
        />
        <CustomInput
          label="Sinopsis"
          name="description"
          placeholder="Proporciona una sinopsis o descripción del libro"
          error={errors.description ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          textarea
          maxInputHeight="125px"
          maxLength={MAX_LENGTH_ITEM.DESCRIPTION}
        />
        <CustomSelect
          label="Tipo de libro"
          name="type"
          options={BOOK_TYPES_OPTIONS}
          onValueChange={handleTypeChange}
          placeholder="Selecciona el tipo de libro"
          defaultValue={[form?.type]}
        />
        <CustomSelect
          label="Sección"
          name="section"
          options={sectionsOptions}
          placeholder="Selecciona la sección a la que pertenece el libro"
          onValueChange={handleSectionChange}
          value={form.sectionId ? [form.sectionId] : []}
          loading={isSectionsLoading}
          error={isSectionsError ? "Error al cargar las secciones" : null}
        />
        <CustomInput
          label="ISBN"
          name="isbn"
          placeholder="Ej.: 1234567890123"
          required
          error={errors.isbn ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH_BOOK.ISBN}
        />
        <Separator />
        <Heading as="h2" size="md" mt={4}>
          {" "}
          Estado de conservación y disponibilidad{" "}
        </Heading>
        <CustomSelect
          label="Estado de conservación"
          name="condition"
          options={ITEM_CONDITIONS_OPTIONS}
          placeholder="Introduce el estado de conservación del libro"
          required
          error={errors.condition ?? ""}
          onValueChange={handleConditionChange}
          defaultValue={[form?.condition]}
        />
        <CustomInput
          label="Comentarios"
          name="comments"
          placeholder="Añade comentarios sobre el estado de conservación del libro"
          textarea
          maxInputHeight="125px"
          maxLength={MAX_LENGTH_ITEM.COMMENTS}
        />
        <CustomSwitch
          checked={form.loanAvailable}
          onChange={(checked) => {
            setForm((prev) => ({ ...prev, loanAvailable: checked }));
          }}
          label="Disponible para préstamo"
        />
        <CustomSwitch
          checked={form.publicated}
          onChange={(checked) => {
            setForm((prev) => ({ ...prev, publicated: checked }));
          }}
          label="Visible en el catálogo"
        />
        <CustomNumberInput
          label="Número de copias"
          defaultValue={form.copies}
          min={1}
          max={10}
          onChange={(value: number) => {
            setForm((prev) => ({
              ...prev,
              copies: value,
              availableCopies: value,
            }));
          }}
        />
        <Heading as="h2" size="md" mt={4}>
          {" "}
          Información sobre la compra{" "}
        </Heading>
        <CustomDateInput
          label="Fecha de compra"
          value={form.purchasedAt}
          error={errors.purchasedAt ?? ""}
          onChange={(e) => setForm((prev) => ({ ...prev, purchasedAt: e }))}
          acceptsFutureDates={false}
        />

        {/* TODO: Implement categories selection. */}
      </FormDialog>
      <CreateSagaDialog
        isOpen={sagaDialogOpen}
        setIsOpen={setSagaDialogOpen}
        setSaga={(sagaName) =>
          setForm((prev) => ({ ...prev, sagaName: sagaName }))
        }
      />
    </>
  );
}
