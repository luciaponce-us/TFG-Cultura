import { useState } from "react";

import { Separator } from "@chakra-ui/react";

import {
  CustomInput,
  CustomSelect,
  FormDialog,
} from "@/modules/core/components";
import {
  handleChange,
  handleSelectChange,
  PLACEHOLDER,
} from "@/modules/core/utils/utils";
import {
  CategoriesSelect,
  CreateCategoryDialog,
} from "@/modules/categories/components";
import { SectionSelect } from "@/modules/sections/components";

import { useBook, useBookForm, useCreateBook, useUpdateBook } from "../hooks";
import {
  BOOK_TYPES_OPTIONS,
  INITIAL_BOOK,
  INITIAL_BOOK_ERRORS,
  type BookErrors,
} from "../types/book";
import type { CreateItemDialogProps } from "../types";
import {
  MAX_LENGTH,
  cleanIsbn,
  validateBookForm,
} from "../validations/book.validations";
import {
  AdminItemInfoForm,
  CreateSagaDialog,
  ItemImageInput,
  SagaSelect,
} from "./";

export function CreateBookDialog({
  isOpen,
  setIsOpen,
  sectionDefaultValue,
  itemId,
}: CreateItemDialogProps) {
  const { data: bookToUpdate } = useBook(itemId);
  const { form, setForm } = useBookForm(itemId, bookToUpdate);
  const [errors, setErrors] = useState<BookErrors>(INITIAL_BOOK_ERRORS);
  const [image, setImage] = useState<File | null>(null);
  const { mutateAsync: createBook, isPending: submitting } = useCreateBook(
    form,
    image,
    setErrors,
    setIsOpen,
  );
  const { mutateAsync: updateBook, isPending: updating } = useUpdateBook(
    itemId,
    form,
    image,
    setErrors,
    setIsOpen,
  );

  const loading = submitting || updating;
  const [sagaDialogOpen, setSagaDialogOpen] = useState(false);
  const [categoryDialogOpen, setCategoryDialogOpen] = useState(false);

  const handleTypeChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "type", form, setErrors, setForm);

  async function handleSubmit() {
    validateBookForm(form, setErrors);

    if (errors != INITIAL_BOOK_ERRORS) return;

    if (itemId) {
      await updateBook();
    } else {
      await createBook();
    }
  }

  return (
    <>
      <FormDialog
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        title={
          itemId ? `Editando "${bookToUpdate?.name || "libro"}"` : "Crear libro"
        }
        handleSubmit={async () => await handleSubmit()}
        submitButtonText={itemId ? "Actualizar" : "Crear"}
        resetForm={() => {
          setForm(INITIAL_BOOK);
          setErrors(INITIAL_BOOK_ERRORS);
          setImage(null);
        }}
      >
        <ItemImageInput
          image={image}
          setImage={setImage}
          loading={loading}
          placeholder={PLACEHOLDER.BOOK}
        />

        <CustomInput
          label="Título"
          name="name"
          placeholder="Introduce el título..."
          required
          error={errors.name ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.NAME}
          defaultValue={form.name}
        />

        <CustomInput
          label="Autor"
          name="author"
          placeholder="Introduce el nombre del autor..."
          required
          error={errors.author ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          defaultValue={form.author}
          maxLength={MAX_LENGTH.AUTHOR}
        />

        <SagaSelect
          form={form}
          setErrors={setErrors}
          setForm={setForm}
          onCreateSaga={() => setSagaDialogOpen(true)}
        />

        <CategoriesSelect
          form={form}
          setForm={setForm}
          onCreateCategory={() => setCategoryDialogOpen(true)}
          error={errors.categoriesIds}
        />

        <CustomInput
          label="Sinopsis"
          name="description"
          placeholder="Proporciona una sinopsis o descripción del libro"
          error={errors.description ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          textarea
          maxInputHeight="240px"
          maxLength={MAX_LENGTH.DESCRIPTION}
          defaultValue={form.description}
        />

        <CustomSelect
          label="Tipo de libro"
          name="type"
          options={BOOK_TYPES_OPTIONS}
          onValueChange={handleTypeChange}
          placeholder="Selecciona el tipo de libro"
          defaultValue={[form?.type]}
        />

        <SectionSelect
          form={form}
          setForm={setForm}
          errors={errors}
          setErrors={setErrors}
          defaultValueText={sectionDefaultValue}
        />

        <CustomInput
          label="ISBN"
          name="isbn"
          placeholder="Ej.: 1234567890123"
          required
          error={errors.isbn ?? ""}
          onChange={(e) => {
            e.target.value = cleanIsbn(e.target.value).slice(
              0,
              MAX_LENGTH.ISBN,
            );
            handleChange(e, form, setErrors, setForm);
          }}
          defaultValue={form.isbn}
        />

        <Separator />
        <AdminItemInfoForm
          form={form}
          setForm={setForm}
          errors={errors}
          setErrors={setErrors}
          loading={loading}
        />
      </FormDialog>
      <CreateSagaDialog
        isOpen={sagaDialogOpen}
        setIsOpen={setSagaDialogOpen}
        setSaga={(sagaName) =>
          setForm((prev) => ({ ...prev, sagaName: sagaName }))
        }
      />
      <CreateCategoryDialog
        isOpen={categoryDialogOpen}
        setIsOpen={setCategoryDialogOpen}
        onCategoryCreated={(category) =>
          setForm((prev) => ({
            ...prev,
            categoriesIds: [...prev.categoriesIds, category.id],
          }))
        }
      />
    </>
  );
}
