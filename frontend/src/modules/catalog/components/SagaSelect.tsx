import { CustomSelect } from "@/modules/core/components";
import { useSagas } from "../hooks";
import { handleSelectChange } from "@/modules/core/utils/utils";
import type { Dispatch, SetStateAction } from "react";
import type { BookErrors, BookRequest } from "../types/book";

interface SagaSelectProps {
  form: BookRequest;
  setErrors: Dispatch<SetStateAction<BookErrors>>;
  setForm: Dispatch<SetStateAction<BookRequest>>;
  onCreateSaga: () => void;
}

export function SagaSelect({
  form,
  setErrors,
  setForm,
  onCreateSaga,
}: SagaSelectProps) {
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

  const handleSagaChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "sagaName", form, setErrors, setForm);

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
