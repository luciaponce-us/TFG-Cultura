import { Field, NumberInput } from "@chakra-ui/react";

export function CustomNumberInput({
  defaultValue,
  onChange,
  label,
  error,
  min,
  max,
}: {
  defaultValue: number;
  onChange: (value: number) => void;
  label: string;
  min?: number;
  max?: number;
  error?: string;
}) {
  return (
    <Field.Root invalid={!!error} onChange={(e) => onChange(Number(e.target))}>
      <Field.Label>{label}</Field.Label>
      <NumberInput.Root
        defaultValue={defaultValue as unknown as string}
        min={min}
        max={max}
        width="80px"
      >
        <NumberInput.Control />
        <NumberInput.Input />
      </NumberInput.Root>
      {error && <Field.ErrorText>{error}</Field.ErrorText>}
    </Field.Root>
  );
}
