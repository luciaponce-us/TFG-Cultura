import { useState } from "react";
import type { BookErrors } from "../types/book";
import { BOOK_TYPES_OPTIONS, INITIAL_BOOK_ERRORS } from "../types/book";
import { useCreateBook, useBook, useBookForm, useUpdateBook } from "../hooks";
import { handleChange, handleSelectChange } from "@/modules/core/utils/utils";
import { HStack, Separator, VStack, Image, Box } from "@chakra-ui/react";
import {
  CustomInput,
  CustomSelect,
  FormDialog,
  UploadBox,
} from "@/modules/core/components";
import {
  MAX_LENGTH as MAX_LENGTH_BOOK,
  cleanIsbn,
  validateBookForm,
} from "../validations/book.validations";
import { MAX_LENGTH as MAX_LENGTH_ITEM } from "../validations/item.validations";
import { CreateSagaDialog, SagaSelect } from "./";
import {
  CategoriesSelect,
  CreateCategoryDialog,
} from "@/modules/categories/components";
import { SectionSelect } from "@/modules/sections/components";
import { AdminItemInfoForm } from "./AdminItemInfoForm";

const BOOK_PLACEHOLDER =
  "https://res.cloudinary.com/dubz79y98/image/upload/v1788778962/book_placeholder.jpg";

interface CreateBookDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: (isOpen: boolean) => void;
  readonly sectionDefaultValue?: string;
  readonly bookId?: string;
}

export function CreateBookDialog({
  isOpen,
  setIsOpen,
  sectionDefaultValue,
  bookId,
}: CreateBookDialogProps) {
  console.log("CreateBookDialog rendered with bookId:", bookId);
  const { data: bookToUpdate } = useBook(bookId);
  const { form, setForm } = useBookForm(bookId, bookToUpdate);
  const [errors, setErrors] = useState<BookErrors>(INITIAL_BOOK_ERRORS);
  const [image, setImage] = useState<File | null>(null);
  const {
    mutateAsync: createBook,
    isPending: submitting,
    isError: isCreateBookError,
  } = useCreateBook(form, image, setErrors, setIsOpen);
  const {mutateAsync: updateBook, isPending: updating, isError: isUpdateBookError} = useUpdateBook(bookId, form, image, setErrors, setIsOpen);

  const loading = submitting || updating;
  const [sagaDialogOpen, setSagaDialogOpen] = useState(false);
  const [categoryDialogOpen, setCategoryDialogOpen] = useState(false);

  const handleTypeChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "type", form, setErrors, setForm);

  async function handleSubmit() {
    const errors = validateBookForm(form);
    setErrors(errors);
    if (Object.values(errors).some(Boolean)) {
      return;
    }
    if (bookId) {
      await updateBook();
      if(!isUpdateBookError) {
        setIsOpen(false);
      }
    } else {
      await createBook();
      if (!isCreateBookError) {
      setIsOpen(false);
    }
    }
  }

  return (
    <>
      <FormDialog
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        title={
          bookId ? `Editando "${bookToUpdate?.name || "libro"}"` : "Crear libro"
        }
        handleSubmit={async () => await handleSubmit()}
        submitButtonText={bookId ? "Actualizar" : "Crear"}
      >
        <HStack
          align="stretch"
          w="100%"
          maxW="100%"
          maxH="200px"
          mb={image ? "60px" : ""}
        >
          <Box aspectRatio={2 / 3} h="auto" maxH="100%" flexShrink={0}>
            <Image
              src={image ? URL.createObjectURL(image) : BOOK_PLACEHOLDER}
              alt="Foto del libro"
              w="100%"
              h="100%"
              objectFit="cover"
              borderRadius="md"
            />
          </Box>
          <VStack flex={1} minW={0}>
            <UploadBox
              text={
                <>
                  Arrastra la <b>foto del libro</b>
                </>
              }
              secondaryText="JPG o PNG, tamaño no superior a 2MB"
              fileType="image/*"
              onFileChange={setImage}
              disabled={loading}
            />
          </VStack>
        </HStack>
        <CustomInput
          label="Título"
          name="name"
          placeholder="Introduce el título..."
          required
          error={errors.name ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH_ITEM.NAME}
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
          maxLength={MAX_LENGTH_BOOK.AUTHOR}
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
          maxLength={MAX_LENGTH_ITEM.DESCRIPTION}
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
              MAX_LENGTH_BOOK.ISBN,
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
