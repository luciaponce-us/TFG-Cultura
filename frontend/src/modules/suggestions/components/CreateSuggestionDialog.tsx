import { Dialog, Heading, VStack } from "@chakra-ui/react";
import type { SuggestionCreateRequest, SuggestionType } from "../types";
import { useState } from "react";
import {
  CustomButton,
  CustomInput,
  CustomSelect,
  toaster,
} from "@/modules/core/components";
import {
  handleChange,
  handleSelectChange,
  isApiError,
} from "@/modules/core/utils/utils";
import { validateSuggestionForm } from "../validations/suggestion.validations";
import { createSuggestion } from "../service/suggestion.service";

const initialForm: SuggestionCreateRequest = {
  title: "",
  description: "",
  type: "OTHER" as SuggestionType,
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
  onClose,
  token,
}: {
  isOpen: boolean;
  onClose: () => void;
  token?: string | null;
}) {
  const [form, setForm] = useState<SuggestionCreateRequest>(initialForm);
  const [errors, setErrors] = useState<SuggestionFormErrors>(initialErrors);
  const [loading, setLoading] = useState(false);

  const handleTypeChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "type", form, setErrors, setForm);


  async function handleSubmit() {
    setLoading(true);
    if (!token) {
      setErrors({ general: "Debes iniciar sesión para crear una sugerencia" });
      setLoading(false);
      return;
    }

    const errors = validateSuggestionForm(form);
    setErrors(errors);
    if (Object.keys(errors).length > 0) {
      setLoading(false);
      return;
    }

    try {
      const res = await createSuggestion(token, form);
      toaster.create({
        title: "Sugerencia creada",
        description: "Tu sugerencia ha sido creada exitosamente",
        type: "success",
      });
      onClose();
      console.log("Sugerencia creada:", res);
    } catch (error) {
      if (isApiError(error)) {
        console.error("Error creating suggestion:", error.message);
        setErrors({
          general:
            "Ocurrió un error al crear la sugerencia. Inténtalo de nuevo.",
        });
      } else {
        console.error("Unexpected error creating suggestion:", error);
        setErrors({
          general: "Ocurrió un error inesperado. Inténtalo de nuevo.",
        });
      }
    } finally {
      setLoading(false);
    }
  }

  return (
    <Dialog.Root open={isOpen} onOpenChange={(open) => !open && onClose()}>
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
            <CustomButton onClick={onClose} color="rojo">
              Cancelar
            </CustomButton>
            <CustomButton onClick={handleSubmit} loading={loading}>
              Crear
            </CustomButton>
          </Dialog.Footer>
        </Dialog.Content>
      </Dialog.Positioner>
    </Dialog.Root>
  );
}
