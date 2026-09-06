import {
  Field,
  Portal,
  Select,
  Spinner,
  createListCollection,
} from "@chakra-ui/react";

interface CustomSelectProps extends Omit<
  React.ComponentProps<typeof Select.Root>,
  "collection"
> {
  label: string;
  placeholder: string;
  options: { label: string; value: string }[];
  error?: string | null;
  loading?: boolean;
  onCreate?: () => void; // Optional callback for creating a new option
  onCreateLabel?: string; // Optional label for the "create new" option
}

export const CustomSelect = ({
  label,
  placeholder,
  options,
  error,
  loading = false,
  onCreate,
  onCreateLabel = "Crear nuevo",
  ...props
}: CustomSelectProps) => {
  const optionsList = createListCollection({ items: options });

  return (
    <Field.Root invalid={!!error}>
      <Select.Root
        collection={optionsList}
        size="sm"
        w="100%"
        disabled={loading}
        {...props}
      >
        <Select.HiddenSelect />
        <Select.Label>{label}</Select.Label>
        <Select.Control>
          <Select.Trigger>
            <Select.ValueText
              placeholder={loading ? "Cargando..." : placeholder}
            />
          </Select.Trigger>
          <Select.IndicatorGroup>
            {loading ? <Spinner size="xs" /> : <Select.Indicator />}
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

              {onCreate && (
                <Select.Item
                  item={{ label: "Crear nuevo", value: "__create_new__" }}
                  onClick={onCreate}
                >
                  {onCreateLabel}
                </Select.Item>
              )}
            </Select.Content>
          </Select.Positioner>
        </Portal>
      </Select.Root>
      {error && <Field.ErrorText>{error}</Field.ErrorText>}
    </Field.Root>
  );
};
