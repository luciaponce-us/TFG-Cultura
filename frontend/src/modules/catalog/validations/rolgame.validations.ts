import { MAX_LENGTH as MAX_LENGTH_ITEM, validateItemForm } from "@/modules/catalog/validations/item.validations";
import type { RolBookType, RolGameErrors, RolGameRequest } from "../types/rolgame";

export const MAX_LENGTH = {
    ...MAX_LENGTH_ITEM}

export function validateRolGameForm(
    form: RolGameRequest
): RolGameErrors {
    const base = validateItemForm(form);
    const errors: RolGameErrors = {
        ...base,
        type: validateType(form.type),
        sagaId: validateSagaId(form.sagaId),
    };

    return Object.fromEntries(
    Object.entries(errors).filter(([, error]) => error !== undefined),
  );
}

function validateType(value: RolBookType): string | undefined {
    if (!value) return "El tipo de juego de rol es obligatorio.";
    return;
}

function validateSagaId(value: string): string | undefined {
    if (!value) return "La saga del juego de rol es obligatoria.";
    return;
}

