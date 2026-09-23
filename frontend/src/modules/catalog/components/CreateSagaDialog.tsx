import { useState, type Dispatch, type SetStateAction } from "react";
import { Box, Spinner } from "@chakra-ui/react";
import { useCreateSaga, useSaga, useSagaForm, useUpdateSaga } from "../hooks";
import { CustomInput, FormDialog } from "@/modules/core/components";
import { isApiError } from "@/modules/core/utils/utils";
import { validateSagaName, MAX_LENGTH } from "../validations/saga.validations";

interface CreateSagaDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: Dispatch<SetStateAction<boolean>>;
  readonly setSaga?: (sagaName: string) => void;
  readonly sagaToEditName?: string;
}

export function CreateSagaDialog({
  isOpen,
  setIsOpen,
  setSaga,
  sagaToEditName,
}: CreateSagaDialogProps) {
  const { data: sagaToUpdate, isLoading: isLoadingSagaToUpdate } =
    useSaga(sagaToEditName);
  const { sagaName, setSagaName } = useSagaForm(
    sagaToEditName,
    sagaToUpdate,
    isLoadingSagaToUpdate,
  );
  const [error, setError] = useState<string | null>(null);
  function resetForm() {
    setSagaName("");
    setError(null);
  }
  const { mutateAsync: createSaga, isPending: creating } = useCreateSaga(
    setError,
    resetForm,
    setIsOpen,
  );
  const { mutateAsync: updateSaga, isPending: updating } = useUpdateSaga(
    sagaToUpdate?.id,
    sagaName,
    setError,
    setIsOpen,
    resetForm,
  );
  const loading = creating || updating || isLoadingSagaToUpdate;

  async function handleSubmit() {
    validateSagaName(sagaName, setError);
    if (error) return;
    if (sagaToEditName && sagaToUpdate) {
      await updateSaga();
    } else {
      await createSaga(sagaName);
      setSaga?.(sagaName);
    }

    setIsOpen(false);
  }

  if (sagaToEditName && isLoadingSagaToUpdate) {
    return null;
  }

  return (
    <FormDialog
      isOpen={isOpen}
      setIsOpen={setIsOpen}
      title={sagaToEditName ? `Editando saga: ${sagaToEditName}` : "Crear saga"}
      handleSubmit={handleSubmit}
      submitButtonText={sagaToEditName ? "Guardar" : "Crear"}
      resetForm={resetForm}
      disabled={loading}
    >
      {isLoadingSagaToUpdate ? (
        <Box
          display="flex"
          justifyContent="center"
          alignItems="center"
          w="100%"
        >
          <Spinner size="xl" color="principal.500" />
        </Box>
      ) : (
        <CustomInput
          label="Nombre de la saga"
          name="sagaName"
          placeholder="Introduce el nombre de la saga..."
          required
          maxLength={MAX_LENGTH.NAME}
          value={sagaName}
          onChange={(e) => setSagaName(e.target.value.trim())}
          error={isApiError(error) ? error.errors?.name : ""}
          disabled={loading}
        />
      )}
    </FormDialog>
  );
}
