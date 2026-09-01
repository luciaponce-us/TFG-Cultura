import { useState } from "react";
import type { BookCreateRequest, BookCreateRequestErrors } from "../types";
import {
  BOOK_TYPES_OPTIONS,
  INITIAL_BOOK,
  INITIAL_BOOK_ERRORS,
  ITEM_CONDITIONS_OPTIONS,
} from "../types";
import { useCreateBook } from "../hooks/useCreateBook";
import { handleChange, handleSelectChange } from "@/modules/core/utils/utils";
import { Dialog, Heading, Separator, VStack } from "@chakra-ui/react";
import {
  CustomButton,
  CustomInput,
  CustomSelect,
  CustomSwitch,
  CustomNumberInput,
  CustomDateInput
} from "@/modules/core/components";
import { MAX_LENGTH as MAX_LENGTH_BOOK, validateBookForm } from "../validations/book.validations";
import { MAX_LENGTH as MAX_LENGTH_ITEM } from "../validations/item.validations";

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
  const { mutateAsync: createBook, isPending: loading } = useCreateBook();
  const [errors, setErrors] =
    useState<BookCreateRequestErrors>(INITIAL_BOOK_ERRORS);

  const handleTypeChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "type", form, setErrors, setForm);

  const handleConditionChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "condition", form, setErrors, setForm);

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
    <Dialog.Root open={isOpen}>
      <Dialog.Backdrop />
      <Dialog.Positioner>
        <Dialog.Content
          maxH="80vh"
          overflow="hidden"
          borderRadius="xl"
          bg="background"
          as="form"
        >
          <Dialog.CloseTrigger />
          <Dialog.Header>
            <Dialog.Title>
              <Heading as="h1">Crear libro</Heading>
            </Dialog.Title>
          </Dialog.Header>
          <Dialog.Body>
            <VStack
              overflowY="scroll"
              maxH="60vh"
              align="stretch"
              gap={4}
              px={4}
              py={2}
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
                onChange={(value:number) => {
                  setForm((prev) => ({ ...prev, copies: value, availableCopies: value }));
                }}
              />
              <Heading as="h2" size="md" mt={4}> Información sobre la compra </Heading>
              <CustomDateInput
                label="Fecha de compra"
                value={form.purchasedAt}
                error={errors.purchasedAt ?? ""}
                onChange={(e) => setForm((prev) => ({ ...prev, purchasedAt: e }))}
                acceptsFutureDates={false}
              />
              
              {/* TODO: Implement saga selection */}
              {/* TODO: Implement section selection */}
              {/* TODO: Implement categories selection */}
            </VStack>
          </Dialog.Body>
          <Dialog.Footer>
            <CustomButton onClick={() => setIsOpen(false)} color="rojo">
              Cancelar
            </CustomButton>
            <CustomButton
              onClick={() => void handleSubmit()}
              loading={loading}
              type="submit"
            >
              Crear
            </CustomButton>
          </Dialog.Footer>
        </Dialog.Content>
      </Dialog.Positioner>
    </Dialog.Root>
  );
}
