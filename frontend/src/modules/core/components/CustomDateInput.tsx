import { DateInput, Field } from "@chakra-ui/react";
import {
  type DateValue,
  parseDate,
  getLocalTimeZone,
} from "@internationalized/date";

export function CustomDateInput({
  label,
  error,
  value,
  onChange,
  acceptsFutureDates = true,
  disabled = false,
}: {
  label: string;
  error?: string;
  value: string;
  onChange: (value: string) => void;
  acceptsFutureDates?: boolean;
  disabled?: boolean;
}) {
  const dateValue: DateValue[] | undefined = value
    ? [parseDate(value)]
    : undefined;

  const handleDateChange = (newValue: DateValue[] | undefined) => {
    const firstValue = newValue?.[0] ?? null;

    if (firstValue) {
      onChange(firstValue.toString());
    } else {
      onChange("");
    }
  };

  const handleValueChange = (event: { value: DateValue[] | undefined }) => {
    handleDateChange(event.value);
  };

  const validateDate = (date: DateValue | null): boolean => {
    if (!date) {
      return true; // Allow empty date
    }

    if (!acceptsFutureDates) {
      const today = new Date();
      const timezone = getLocalTimeZone();
      return date.toDate(timezone).getTime() <= today.getTime();
    }

    return true;
  };

  return (
    <Field.Root invalid={!!error || !validateDate(dateValue?.[0] ?? null)}>
      <DateInput.Root
        value={dateValue}
        onValueChange={handleValueChange}
        locale="es-ES"
        disabled={disabled}
        invalid={!!error || !validateDate(dateValue?.[0] ?? null)}
      >
        <DateInput.Label>{label}</DateInput.Label>
        <DateInput.Control>
          <DateInput.Segments />
        </DateInput.Control>
        <DateInput.HiddenInput />
      </DateInput.Root>
      {error && <Field.ErrorText>{error}</Field.ErrorText>}
    </Field.Root>
  );
}
