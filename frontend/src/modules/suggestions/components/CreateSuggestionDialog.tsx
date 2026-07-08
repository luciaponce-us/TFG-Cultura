import { Dialog, Heading, VStack } from "@chakra-ui/react";
import { useState } from "react";

import {
  CustomButton,
  CustomInput,
  CustomSelect,
} from "@/modules/core/components";
import { handleChange, handleSelectChange } from "@/modules/core/utils/utils";

import { useCreateSuggestion } from "../hooks";
import { validateSuggestionForm } from "../validations/suggestion.validations";

import type { SuggestionCreateRequest } from "../types";

const initialForm: SuggestionCreateRequest = {
  title: "",
  description: "",
  type: "OTHER",
};

type SuggestionFormErrors = Partial<
  Record<keyof SuggestionCreateRequest, string>
> & {
  general?: string;
};

const initialErrors: SuggestionFormErrors = {
  title: "",
  description: "",
  type: "",
  general: "",
};

export function CreateSuggestionDialog({
  isOpen,
  setIsOpen,
  token,
}: {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  token?: string | null;
}) {
  const [form, setForm] = useState<SuggestionCreateRequest>(initialForm);
  const { mutateAsync: createSuggestion, isPending: loading } =
    useCreateSuggestion();
  const [errors, setErrors] = useState<SuggestionFormErrors>(initialErrors);

  const handleTypeChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "type", form, setErrors, setForm);

  async function handleSubmit() {
    const errors = validateSuggestionForm(form, token);
    setErrors(errors);
    if (Object.keys(errors).length > 0) {
      return;
    }
    await createSuggestion(form);
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
        >
          <Dialog.CloseTrigger />
          <Dialog.Header>
            <Dialog.Title>
              <Heading as="h1">Crear sugerencia</Heading>
            </Dialog.Title>
          </Dialog.Header>
          <Dialog.Body>
            <VStack>
              <CustomInput
                label="Título"
                name="title"
                placeholder="Describe brevemente la sugerencia"
                required
                error={errors.title ?? ""}
                onChange={(e) => handleChange(e, form, setErrors, setForm)}
              />
              <CustomInput
                label="Descripción"
                name="description"
                placeholder="Proporciona una descripción detallada de la sugerencia"
                error={errors.description ?? ""}
                onChange={(e) => handleChange(e, form, setErrors, setForm)}
                textarea
                maxInputHeight="125px"
              />
              <CustomSelect
                label="Tipo de sugerencia"
                name="type"
                options={[
                  { value: "CATALOG", label: "Catálogo" },
                  { value: "EVENT", label: "Evento" },
                  { value: "OTHER", label: "Otro" },
                ]}
                onValueChange={handleTypeChange}
                placeholder="Selecciona el tipo de sugerencia"
                defaultValue={[form?.type as string]}
              />
            </VStack>
          </Dialog.Body>
          <Dialog.Footer>
            <CustomButton onClick={() => setIsOpen(false)} color="rojo">
              Cancelar
            </CustomButton>
            <CustomButton onClick={() => void handleSubmit()} loading={loading}>
              Crear
            </CustomButton>
          </Dialog.Footer>
        </Dialog.Content>
      </Dialog.Positioner>
    </Dialog.Root>
  );
}
