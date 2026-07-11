import { Button, Field, Input, InputGroup, Textarea } from "@chakra-ui/react";
import { useState } from "react";
import { IconEye, IconEyeOff } from "@tabler/icons-react";

interface InputFieldProps {
  label: string;
  name: string;
  placeholder?: string;
  required?: boolean;
  error?: string;
  maxLength?: number;
  showMaxLength?: boolean;
  onChange?: (
    e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => void;
  password?: boolean;
  defaultValue?: string;
  textarea?: boolean;
  maxInputHeight?: string;
}

export const CustomInput = ({
  label,
  name,
  placeholder,
  required = false,
  error,
  maxLength,
  showMaxLength = true,
  onChange,
  password = false,
  defaultValue,
  textarea = false,
  maxInputHeight
}: InputFieldProps) => {
  const [show, setShow] = useState(false);
  const [length, setLength] = useState(defaultValue?.length?? 0);

  const handleChange = (
  e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
) => {
  setLength(e.target.value.length);
  onChange?.(e);
};

const commonProps = {
  name,
  placeholder,
  onChange: handleChange,
  focusRingColor: "principal.600",
  defaultValue,
  maxLength,
};
  return (
    <Field.Root invalid={!!error} required={required}>
      <Field.Label>
        {label} {required && <Field.RequiredIndicator />}
      </Field.Label>
      {password && (
        <InputGroup
          endElement={
            <Button variant="ghost" size="sm" onClick={() => setShow(!show)}>
              {show ? <IconEyeOff size={18} /> : <IconEye size={18} />}
            </Button>
          }
        >
          <Input
            {...commonProps}
            type={show ? "text" : "password"}

          />
        </InputGroup>
      )}
      {!password && !textarea && (
        <Input
          {...commonProps}
        />
      )}
      {textarea && (
        <Textarea
          {...commonProps}
          maxH={maxInputHeight}
          minH="40px"
        />
      )}

      {maxLength && showMaxLength && length >= maxLength * 0.8 && (
  <Field.HelperText textAlign="right" color={length > maxLength ? "red.500" : "gray.500"}>
    {length}/{maxLength}
  </Field.HelperText>
)}

      {error && <Field.ErrorText>{error}</Field.ErrorText>}
    </Field.Root>
  );
};
