import { useState, type Dispatch, type SetStateAction } from "react";
import type { Saga } from "../../types/saga";

export function useSagaForm(
  sagaId: string | undefined,
  sagaToUpdate?: Saga,
  isLoading = false,
): { sagaName: string; setSagaName: Dispatch<SetStateAction<string>> } {
  const [formOverride, setFormOverride] = useState<{
    sagaId: string | undefined;
    value: string;
  }>();

  const loadedForm = sagaToUpdate ? sagaToUpdate.name : "";

  const sagaName =
    formOverride?.sagaId === sagaId && formOverride !== undefined
      ? formOverride.value
      : loadedForm;

  const setSagaName: Dispatch<SetStateAction<string>> = (nextForm) => {
    if (sagaId && isLoading) {
      return; // Esperando a que se cargue la saga de juegos de rol a editar
    }

    setFormOverride((currentOverride) => {
      const currentForm =
        currentOverride?.sagaId === sagaId && currentOverride !== undefined
          ? currentOverride.value
          : loadedForm;

      return {
        sagaId,
        value:
          typeof nextForm === "function" ? nextForm(currentForm) : nextForm,
      };
    });
  };

  return { sagaName, setSagaName };
}
