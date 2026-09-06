import { useState } from "react";
import { useCreateSaga } from "../hooks";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { Dialog, Heading, VStack } from "@chakra-ui/react";
import { CustomButton, CustomInput } from "@/modules/core/components";
import { MAX_LENGTH } from "../validations/item.validations";
import { isApiError } from "@/modules/core/utils/utils";

export function CreateSagaDialog({
  isOpen,
  setIsOpen,
  setSaga,
}: {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  setSaga?: (sagaName: string) => void;
}) {
  const [sagaName, setSagaName] = useState<string>("");
  const {
    mutateAsync: createSaga,
    isPending: loading,
    error: error,
  } = useCreateSaga();

  async function handleSubmit() {
    if (!sagaName) {
      toaster.create({
        title: "Error al crear saga",
        description: "El nombre de la saga no puede estar vacío.",
        type: "error",
      });
      return;
    }
    await createSaga(sagaName);
    setSaga?.(sagaName);
    setIsOpen(false);
  }

  return (
    <Dialog.Root open={isOpen}>
      <Dialog.Backdrop />
      <Dialog.Positioner>
        <Dialog.Content
          aria-label="Crear saga"
          css={{
            width: "100%",
            maxWidth: "500px",
            borderRadius: "8px",
            backgroundColor: "$background",
            boxShadow: "0px 4px 16px rgba(0, 0, 0, 0.1)",
          }}
          as="form"
          onSubmit={(e) => {
            e.preventDefault();
            void handleSubmit();
          }}
        >
          <Dialog.CloseTrigger />
          <Dialog.Header>
            <Dialog.Title>
              <Heading as="h1">Crear saga</Heading>
            </Dialog.Title>
          </Dialog.Header>
          <Dialog.Body>
            <VStack align="stretch" gap={4} px={4} py={2}>
              <CustomInput
                label="Nombre de la saga"
                name="sagaName"
                placeholder="Introduce el nombre de la saga..."
                required
                maxLength={MAX_LENGTH.NAME}
                onChange={(e) => setSagaName(e.target.value)}
                error={isApiError(error) ? error.errors?.name : ""}
              />
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
