import type { Dispatch, SetStateAction } from "react";
import {
  ITEM_CONDITIONS_OPTIONS,
  type ItemErrors,
  type ItemRequest,
} from "../types";
import { Heading, HStack, Separator } from "@chakra-ui/react";
import {
  CustomDateInput,
  CustomInput,
  CustomNumberInput,
  CustomSelect,
  CustomSwitch,
} from "@/modules/core/components";
import { MAX_LENGTH } from "../validations/item.validations";
import { handleChange, handleSelectChange } from "@/modules/core/utils/utils";

interface AdminItemInfoFormProps<R extends ItemRequest, E extends ItemErrors> {
  form: R;
  setForm: Dispatch<SetStateAction<R>>;
  errors: E;
  setErrors: Dispatch<SetStateAction<E>>;
  loading?: boolean;
  loanAvailable?: boolean;
}

export function AdminItemInfoForm<R extends ItemRequest, E extends ItemErrors>({
  form,
  setForm,
  errors,
  setErrors,
  loading = false,
  loanAvailable = true,
}: AdminItemInfoFormProps<R, E>) {
  const handleConditionChange = ({ value }: { value: string[] }) =>
    handleSelectChange(
      value,
      "condition",
      form,
      setErrors as unknown as Dispatch<
        SetStateAction<Partial<Record<keyof R, string>>>
      >,
      setForm,
    );
  return (
    <>
      <Heading as="h2" size="md" mt={4}>
        Estado de conservación y disponibilidad
      </Heading>
      <CustomSelect
        label="Estado de conservación"
        name="condition"
        options={ITEM_CONDITIONS_OPTIONS}
        placeholder="Introduce el estado de conservación"
        required
        error={errors.condition ?? ""}
        onValueChange={handleConditionChange}
        value={[form.condition]}
        disabled={loading}
      />
      <CustomInput
        label="Comentarios"
        name="comments"
        placeholder="Añade comentarios sobre el estado de conservación"
        textarea
        maxInputHeight="125px"
        maxLength={MAX_LENGTH.COMMENTS}
        error={errors.comments ?? ""}
        onChange={(e) =>
          handleChange(
            e,
            form,
            setErrors as unknown as Dispatch<
              SetStateAction<Partial<Record<keyof R, string>>>
            >,
            setForm,
          )
        }
        defaultValue={form.comments}
        disabled={loading}
      />
      <CustomSwitch
        checked={form.loanAvailable}
        onChange={(checked) =>
          setForm((prev) => ({ ...prev, loanAvailable: checked }))
        }
        label="Disponible para préstamo"
        disabled={loading || !loanAvailable}
      />
      <CustomSwitch
        checked={form.publicated}
        onChange={(checked) =>
          setForm((prev) => ({ ...prev, publicated: checked }))
        }
        label="Visible en el catálogo"
        disabled={loading}
      />

      <Separator />
      <Heading as="h2" size="md" mt={4}>
        Información sobre la compra
      </Heading>
      <CustomDateInput
        label="Fecha de compra"
        value={form.purchasedAt}
        error={errors.purchasedAt ?? ""}
        onChange={(value) =>
          setForm((prev) => ({ ...prev, purchasedAt: value }))
        }
        acceptsFutureDates={false}
        disabled={loading}
      />
      <HStack>
        <CustomNumberInput
          label="Número de copias"
          defaultValue={form.copies}
          min={1}
          max={10}
          required
          error={errors.copies}
          onChange={(value) =>
            setForm((prev) => ({
              ...prev,
              copies: value,
              availableCopies: value,
            }))
          }
          disabled={loading}
        />
        <CustomNumberInput
          label="Precio de compra"
          defaultValue={form.price}
          min={0}
          max={1000}
          step={0.01}
          allowMouseWheel
          disabled={loading}
          error={errors.price}
          onChange={(value) => setForm((prev) => ({ ...prev, price: value }))}
          isEuros
        />
      </HStack>
    </>
  );
}
