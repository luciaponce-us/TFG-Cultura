import { Input, InputGroup, CloseButton } from "@chakra-ui/react";
import { IconSearch } from "@tabler/icons-react";
import { useRef, useState } from "react";

interface CustomSearchBarProps {
  readonly onChange: (e: React.ChangeEvent<HTMLInputElement>) => void;
  readonly placeholder?: string;
  readonly value?: string;
}

export function CustomSearchBar({
  onChange,
  placeholder,
  value,
}: CustomSearchBarProps) {
  const [internalValue, setInternalValue] = useState<string>("");
  const inputRef = useRef<HTMLInputElement | null>(null);
  const inputValue = value ?? internalValue;

  const endElement = inputValue ? (
    <CloseButton
      size="xs"
      onClick={() => {
        const clearedEvent = {
          currentTarget: { value: "" },
        } as React.ChangeEvent<HTMLInputElement>;
        if (value === undefined) {
          setInternalValue("");
        }
        onChange(clearedEvent);
        inputRef.current?.focus();
      }}
      me="-2"
      borderRadius="full"
    />
  ) : (
    <IconSearch size={18} />
  );
  return (
    <InputGroup color="principal.800" endElement={endElement} w="100%">
      <Input
        placeholder={placeholder || "Buscar..."}
        {...style}
        _hover={hoverStyle}
        _focus={focusStyle}
        onChange={(e) => {
          if (value === undefined) {
            setInternalValue(e.currentTarget.value);
          }
          onChange(e);
        }}
        value={inputValue}
        ref={inputRef}
        w="100%"
      />
    </InputGroup>
  );
}

const style = {
  borderRadius: "full",
  px: "20px",
  h: "40px",
  border: "1px solid",
  borderColor: "gray.200",
  transition: "all 0.2s",
  color: "gray.700",
};

const hoverStyle = {
  borderColor: "gray.300",
};

const focusStyle = {
  outline: "none",
  borderColor: "principal.500",
  boxShadow: "0 0 0 3px rgba(75,117,157,0.15)",
};
