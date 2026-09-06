import { useState } from "react";
import { useCreateSaga } from "../hooks";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { CustomInput, FormDialog } from "@/modules/core/components";
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
    <FormDialog
      isOpen={isOpen}
      setIsOpen={setIsOpen}
      title="Crear saga"
      handleSubmit={handleSubmit}
      submitButtonText="Crear"
    >
      <CustomInput
        label="Nombre de la saga"
        name="sagaName"
        placeholder="Introduce el nombre de la saga..."
        required
        maxLength={MAX_LENGTH.NAME}
        onChange={(e) => setSagaName(e.target.value)}
        error={isApiError(error) ? error.errors?.name : ""}
      />
    </FormDialog>
  );
}
