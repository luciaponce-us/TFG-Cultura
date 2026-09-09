import { useState } from "react";
import type { BookCreateRequest, BookCreateRequestErrors } from "../types";
import {
  BOOK_TYPES_OPTIONS,
  INITIAL_BOOK,
  INITIAL_BOOK_ERRORS,
  ITEM_CONDITIONS_OPTIONS,
} from "../types";
import { useCreateBook } from "../hooks";
import {
  handleChange,
  handleSelectChange,
} from "@/modules/core/utils/utils";
import {
  Heading,
  HStack,
  Separator,
  VStack,
  Image,
  Box,
} from "@chakra-ui/react";
import {
  CustomInput,
  CustomSelect,
  CustomSwitch,
  CustomNumberInput,
  CustomDateInput,
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
import { CategoriesSelect } from "@/modules/categories/components";
import { SectionSelect } from "@/modules/sections/components";

const BOOK_PLACEHOLDER =
  "https://res.cloudinary.com/dubz79y98/image/upload/v1788778962/book_placeholder.jpg";

interface CreateBookDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: (isOpen: boolean) => void;
  readonly token?: string | null;
}

export function CreateBookDialog({
  isOpen,
  setIsOpen,
  token,
}: CreateBookDialogProps) {
  const [form, setForm] = useState<BookCreateRequest>(INITIAL_BOOK);
  const [errors, setErrors] =
    useState<BookCreateRequestErrors>(INITIAL_BOOK_ERRORS);
  const [image, setImage] = useState<File | null>(null);
  const {
    mutateAsync: createBook,
    isPending: submitting,
    isError: isCreateBookError
  } = useCreateBook(form, image, setErrors, setIsOpen);

  const [sagaDialogOpen, setSagaDialogOpen] = useState(false);

  const handleTypeChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "type", form, setErrors, setForm);

  const handleConditionChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "condition", form, setErrors, setForm);

  async function handleSubmit() {
    const errors = validateBookForm(form, token);
    setErrors(errors);
    if (Object.values(errors).some(Boolean)) {
      return;
    }
    await createBook();
    if (!isCreateBookError) {
      setIsOpen(false);
    }
  }

  return (
    <>
      <FormDialog
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        title="Crear libro"
        handleSubmit={async () => await handleSubmit()}
        submitButtonText="Crear"
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
              disabled={submitting}
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

        <SagaSelect
          form={form}
          setErrors={setErrors}
          setForm={setForm}
          onCreateSaga={() => setSagaDialogOpen(true)}
        />

        <CategoriesSelect form={form} setForm={setForm} />

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

        <SectionSelect
          form={form}
          setForm={setForm}
          errors={errors}
          setErrors={setErrors}
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

        <Separator />

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

        <HStack>
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

          <CustomNumberInput
            label="Precio de compra"
            defaultValue={form.price}
            min={0}
            max={1000}
            step={0.01}
            allowMouseWheel
            disabled={submitting}
            onChange={(value: number) => {
              setForm((prev) => ({
                ...prev,
                price: value,
              }));
            }}
            isEuros
          />
        </HStack>
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
