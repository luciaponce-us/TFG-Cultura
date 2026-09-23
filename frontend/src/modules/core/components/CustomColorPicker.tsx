import {
  ColorPicker,
  Field,
  HStack,
  Portal,
  parseColor,
} from "@chakra-ui/react";

interface CustomColorPickerProps {
  label?: string;
  name?: string;
  required?: boolean;
  onChange?: (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => void;
  defaultValue?: string;
  error?: string;
}

export function CustomColorPicker({
  label = "Color",
  name = "color",
  required = false,
  onChange,
  defaultValue,
  error,
}: CustomColorPickerProps) {
  return (
    <Field.Root invalid={!!error} required={required}>
      <ColorPicker.Root
        value={defaultValue ? parseColor(defaultValue) : undefined}
        format="hsla"
        onValueChange={(e) => {
          onChange?.({
            target: { name, value: e.value.toString("hex") },
          } as React.ChangeEvent<HTMLInputElement>);
        }}
        maxW="200px"
        required={required}
      >
        <ColorPicker.HiddenInput />
        <ColorPicker.Label>{label}</ColorPicker.Label>
        <ColorPicker.Control>
          <ColorPicker.Input />
          <ColorPicker.Trigger />
        </ColorPicker.Control>
        <Portal>
          <ColorPicker.Positioner>
            <ColorPicker.Content>
              <ColorPicker.Area />
              <HStack>
                <ColorPicker.EyeDropper size="xs" variant="outline" />
                <ColorPicker.Sliders />
              </HStack>
            </ColorPicker.Content>
          </ColorPicker.Positioner>
        </Portal>
      </ColorPicker.Root>
      {error && <Field.ErrorText>{error}</Field.ErrorText>}
    </Field.Root>
  );
}
