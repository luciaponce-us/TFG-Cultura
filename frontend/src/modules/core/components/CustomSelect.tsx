import { Field, Portal, Select, createListCollection } from "@chakra-ui/react";

interface CustomSelectProps extends Omit<
  React.ComponentProps<typeof Select.Root>,
  "collection"
> {
  label: string;
  placeholder: string;
  error?: string;
  options: { label: string; value: string }[];
}

export const CustomSelect = ({
  label,
  placeholder,
  options,
  error,
  ...props
}: CustomSelectProps) => {
  const optionsList = createListCollection({ items: options });

  return (
    <Field.Root invalid={!!error}>
      <Select.Root collection={optionsList} size="sm" w="100%" {...props}>
        <Select.HiddenSelect />
        <Select.Label>{label}</Select.Label>
        <Select.Control>
          <Select.Trigger>
            <Select.ValueText placeholder={placeholder} />
          </Select.Trigger>
          <Select.IndicatorGroup>
            <Select.Indicator />
          </Select.IndicatorGroup>
        </Select.Control>
        <Portal>
          <Select.Positioner>
            <Select.Content>
              {options.map((option) => (
                <Select.Item item={option} key={option.value}>
                  {option.label}
                  <Select.ItemIndicator />
                </Select.Item>
              ))}
            </Select.Content>
          </Select.Positioner>
        </Portal>
      </Select.Root>
      {error && <Field.ErrorText>{error}</Field.ErrorText>}
    </Field.Root>
  );
};
