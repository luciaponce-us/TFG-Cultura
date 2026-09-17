import { useState, type Dispatch, type SetStateAction } from "react";
import { toRolGameRequest } from "../../utils/item.utils";
import {
  INITIAL_ROL_GAME,
  type RolGame,
  type RolGameRequest,
} from "../../types/rolgame";
import { useSectionNameContains } from "@/modules/sections/hooks/useSectionNameContains";

export function useRolGameForm(
  rolGameId: string | undefined,
  sagaId: string,
  rolGameToUpdate?: RolGame,
): { form: RolGameRequest; setForm: Dispatch<SetStateAction<RolGameRequest>> } {
  const [formOverride, setFormOverride] = useState<{
    rolGameId: string | undefined;
    value: RolGameRequest;
  }>();

  const sectionId = useSectionNameContains("rol")?.id;
  const loadedForm = rolGameToUpdate
    ? toRolGameRequest(rolGameToUpdate)
    : { ...INITIAL_ROL_GAME, sagaId: sagaId, sectionId: sectionId };

  const form =
    formOverride?.rolGameId === rolGameId && formOverride !== undefined
      ? formOverride.value
      : loadedForm;

  const setForm: Dispatch<SetStateAction<RolGameRequest>> = (nextForm) => {
    setFormOverride((currentOverride) => {
      const currentForm =
        currentOverride?.rolGameId === rolGameId &&
        currentOverride !== undefined
          ? currentOverride.value
          : loadedForm;

      return {
        rolGameId,
        value:
          typeof nextForm === "function" ? nextForm(currentForm) : nextForm,
      };
    });
  };

  return { form, setForm };
}
