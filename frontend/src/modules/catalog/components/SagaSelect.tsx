import { CustomSelect } from "@/modules/core/components";
import { useSagas } from "../hooks";
import type { Dispatch, SetStateAction } from "react";
import type { ItemErrors, ItemRequest } from "../types";

interface SagaSelectProps<T extends ItemRequest & { sagaName?: string }, E extends ItemErrors & { sagaName?: string }> {
  form: T;
  setErrors: Dispatch<SetStateAction<E>>;
  setForm: Dispatch<SetStateAction<T>>;
  onCreateSaga: () => void;
}

export function SagaSelect<T extends ItemRequest & { sagaName?: string }, E extends ItemErrors & { sagaName?: string }>({
  form,
  setErrors,
  setForm,
  onCreateSaga,
}: SagaSelectProps<T, E>) {
  const {
    data: sagas,
    isLoading: isSagasLoading,
    isError: isSagasError,
  } = useSagas();

  const sagasOptions: { value: string; label: string }[] =
    sagas?.map((saga) => ({
      value: saga.name,
      label: saga.name,
    })) || [];

  const handleSagaChange = ({ value }: { value: string[] }) => {
    setErrors({} as E);
    setForm((previous) => ({ ...previous, sagaName: value[0] ?? "" }));
  };

  return (
    <CustomSelect
      label="Saga"
      name="saga"
      options={sagasOptions}
      placeholder="Selecciona la saga a la que pertenece el libro"
      onValueChange={handleSagaChange}
      value={form.sagaName ? [form.sagaName] : []}
      loading={isSagasLoading}
      error={isSagasError ? "Error al cargar las sagas" : null}
      onCreate={onCreateSaga}
      onCreateLabel="Crear nueva saga"
    />
  );
}
