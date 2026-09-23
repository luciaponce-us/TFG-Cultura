import { Switch } from "@chakra-ui/react";

export function CustomSwitch({
  checked,
  onChange,
  label,
  disabled = false,
}: {
  checked: boolean;
  onChange: (checked: boolean) => void;
  label: string;
  disabled?: boolean;
}) {
  return (
    <Switch.Root
      checked={checked}
      onCheckedChange={(e) => {
        onChange(e.checked);
      }}
      size="md"
      mt={2}
      disabled={disabled}
    >
      <Switch.HiddenInput />

      <Switch.Control
        _checked={{
          bg: "principal.500",
        }}
      />

      <Switch.Label>{label}</Switch.Label>
    </Switch.Root>
  );
}
