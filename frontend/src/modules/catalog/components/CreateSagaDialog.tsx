import { useState } from "react";
import { useCreateSaga } from "../hooks";
import { CustomInput, FormDialog } from "@/modules/core/components";
import { isApiError } from "@/modules/core/utils/utils";
import { validateSagaName, MAX_LENGTH } from "../validations/saga.validations";

interface CreateSagaDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: (isOpen: boolean) => void;
  readonly setSaga?: (sagaName: string) => void;
}

export function CreateSagaDialog({
  isOpen,
  setIsOpen,
  setSaga,
}: CreateSagaDialogProps) {
  const [sagaName, setSagaName] = useState<string>("");
  const [error, setError] = useState<string | null>(null);
  const { mutateAsync: createSaga } = useCreateSaga(setError);

  async function handleSubmit() {
    validateSagaName(sagaName, setError);
    if (error) return;
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
      resetForm={() => {
        setSagaName("");
      }}
    >
      <CustomInput
        label="Nombre de la saga"
        name="sagaName"
        placeholder="Introduce el nombre de la saga..."
        required
        maxLength={MAX_LENGTH.NAME}
        onChange={(e) => setSagaName(e.target.value.trim())}
        error={isApiError(error) ? error.errors?.name : ""}
      />
    </FormDialog>
  );
}
