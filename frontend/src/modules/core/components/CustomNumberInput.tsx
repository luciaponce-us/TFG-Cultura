import { Field, HStack, NumberInput, Text } from "@chakra-ui/react";

interface CustomNumberInputProps extends Omit<
  NumberInput.RootProps,
  "defaultValue" | "onChange" | "value"
> {
  defaultValue: number;
  value?: number | undefined;
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
  value,
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
          {...(value === undefined
            ? { defaultValue: defaultValue as unknown as string }
            : { value: value.toString() })}
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
