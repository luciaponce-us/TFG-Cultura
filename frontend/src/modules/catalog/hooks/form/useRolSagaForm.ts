import { useState, type Dispatch, type SetStateAction } from "react";
import { toRolSagaRequest } from "../../utils/item.utils";
import {
  INITIAL_ROL_SAGA,
  type RolSaga,
  type RolSagaRequest,
} from "../../types/rolgame";
import { useSectionNameContains } from "@/modules/sections/hooks/useSectionNameContains";

export function useRolSagaForm(
  rolSagaId: string | undefined,
  rolSagaToUpdate?: RolSaga,
  isLoading = false,
): { form: RolSagaRequest; setForm: Dispatch<SetStateAction<RolSagaRequest>> } {
  const [formOverride, setFormOverride] = useState<{
    rolSagaId: string | undefined;
    value: RolSagaRequest;
  }>();

    const initialSectionId = useSectionNameContains("rol")?.id;

  const loadedForm = rolSagaToUpdate
    ? toRolSagaRequest(rolSagaToUpdate)
    : {...INITIAL_ROL_SAGA, sectionId: initialSectionId};

  const form =
    formOverride?.rolSagaId === rolSagaId && formOverride !== undefined
      ? formOverride.value
      : loadedForm;

  const setForm: Dispatch<SetStateAction<RolSagaRequest>> = (nextForm) => {
    if (rolSagaId && isLoading) {
      return; // Esperando a que se cargue la saga de juegos de rol a editar
    }

    setFormOverride((currentOverride) => {
      const currentForm =
        currentOverride?.rolSagaId === rolSagaId &&
        currentOverride !== undefined
          ? currentOverride.value
          : loadedForm;

      return {
        rolSagaId,
        value:
          typeof nextForm === "function" ? nextForm(currentForm) : nextForm,
      };
    });
  };

  return { form, setForm };
}
