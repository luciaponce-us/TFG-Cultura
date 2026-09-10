import { Field, HStack, NumberInput, Text } from "@chakra-ui/react";

interface CustomNumberInputProps extends Omit<
  NumberInput.RootProps,
  "defaultValue" | "onChange"
> {
  defaultValue: number;
  onChange: (value: number) => void;
  label: string;
  min?: number;
  max?: number;
  required?: boolean;
  error?: string;
  isEuros?: boolean;
}

export function CustomNumberInput({
  defaultValue,
  onChange,
  label,
  error,
  min,
  max,
  required = false,
  isEuros = false,
  ...props
}: CustomNumberInputProps) {
  return (
    <Field.Root invalid={!!error} required={required}>
      <Field.Label>
        {label} {required && <Field.RequiredIndicator />}
      </Field.Label>
      <HStack>
        <NumberInput.Root
          defaultValue={defaultValue as unknown as string}
          min={min}
          max={max}
          onValueChange={({ valueAsNumber }) => onChange(valueAsNumber)}
          {...props}
          width="80px"
        >
          <NumberInput.Control />
          <NumberInput.Input />
        </NumberInput.Root>
        {isEuros && <Text>€</Text>}
      </HStack>
      {error && <Field.ErrorText>{error}</Field.ErrorText>}
    </Field.Root>
  );
}
