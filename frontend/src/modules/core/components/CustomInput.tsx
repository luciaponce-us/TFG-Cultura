import { Button, Field, Input, InputGroup, Textarea } from "@chakra-ui/react";
import { useState } from "react";
import { IconEye, IconEyeOff } from "@tabler/icons-react";

interface InputFieldProps {
  label: string;
  name: string;
  placeholder?: string;
  required?: boolean;
  error?: string;
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
  onChange,
  password = false,
  defaultValue,
  textarea = false,
  maxInputHeight,
}: InputFieldProps) => {
  const [show, setShow] = useState(false);

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
            name={name}
            placeholder={placeholder}
            type={show ? "text" : "password"}
            onChange={onChange}
            focusRingColor="principal.600"
            defaultValue={defaultValue}
          />
        </InputGroup>
      )}
      {!password && !textarea && (
        <Input
          name={name}
          placeholder={placeholder}
          onChange={onChange}
          focusRingColor="principal.600"
          defaultValue={defaultValue}
        />
      )}
      {textarea && (
        <Textarea
          name={name}
          placeholder={placeholder}
          maxH={maxInputHeight}
          minH="40px"
          onChange={onChange}
          defaultValue={defaultValue}
        />
      )}

      {error && <Field.ErrorText>{error}</Field.ErrorText>}
    </Field.Root>
  );
};
