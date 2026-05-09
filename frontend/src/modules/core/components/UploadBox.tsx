import { Flex, Text, Button, VStack, FileUpload } from "@chakra-ui/react";
import { IconCloudUpload } from "@tabler/icons-react";
import { TextSecondary } from "./text";
import { useState } from "react";

type UploadBoxProps = {
  readonly text: React.ReactNode;
  readonly secondaryText: string;
  readonly fileType: FileUpload.FileMimeType;
  readonly required?: boolean;
  readonly onFileChange?: (file: File | null) => void;
};

export function UploadBox({
  text,
  secondaryText,
  fileType,
  required = false,
  onFileChange
}: UploadBoxProps) {
  const [errors, setErrors] = useState<string[]>([]);

  function getErrorMessage(details: FileUpload.FileRejectDetails) {
    setErrors([])
    const errorTypes = details.files[0].errors;
    const newErrors : string[] = [];

    if (errorTypes.includes("FILE_TOO_LARGE")) {
      newErrors.push("El archivo es demasiado grande. El tamaño máximo permitido es de 2MB.")
    }

    if (errorTypes.includes("FILE_INVALID_TYPE")) {
      let allowedFileType = "desconocido";
      if (fileType == "application/pdf") allowedFileType = "archivos PDF";
      if (fileType == "image/*") allowedFileType = "imágenes";
      newErrors.push(`El tipo de archivo no es válido. Solo se permiten ${allowedFileType}.`);
    }
    const isUnknownError = !errorTypes.includes("FILE_TOO_LARGE") && !errorTypes.includes("FILE_INVALID_TYPE") && errorTypes.length > 0;
    if (isUnknownError) {
      setErrors([`Archivo rechazado por razones desconocidas: ${errorTypes[0]}`]);
    }
    setErrors(newErrors)
  }

  function handleAccept(details: FileUpload.FileAcceptDetails) {
    const file = details.files[0] ?? null;

    setErrors([]);
    onFileChange?.(file);
  }

  return (
    <FileUpload.Root
      maxFiles={1}
      allowDrop={true}
      maxFileSize={2 * 1024 * 1024}
      accept={fileType}
      required={required}
      onFileAccept={handleAccept}
      onFileReject={getErrorMessage}
    >
      <FileUpload.HiddenInput />
      <FileUpload.Dropzone
        border="2px dashed"
        borderColor={errors.length > 0 ? "fg.error" : "gray.300"}
        borderRadius="xl"
        p={6}
        w="100%"
        minH="unset"
        bg="gray.50"
        flexWrap="wrap"
        color="principal.800"
      >
        <FileUpload.DropzoneContent>
          <Flex align="center" justify="space-between" gap={4}>
            {/* Parte izquierda */}
            <Flex align="center" gap={4}>
              <IconCloudUpload stroke={1} height="50px" width="50px" />

              <VStack align="start" gap={0}>
                <Text>{text}</Text>
                <TextSecondary>{secondaryText}</TextSecondary>
              </VStack>
            </Flex>

            {/* Botón */}

            <FileUpload.Trigger asChild>
              <Button
                bg="principal.500"
                color="white"
                borderRadius="full"
                _hover={{ bg: "principal.600" }}
              >
                Seleccionar archivo
              </Button>
            </FileUpload.Trigger>
          </Flex>
        </FileUpload.DropzoneContent>
      </FileUpload.Dropzone>

      {errors.map((error) => (
        <Text key={error} color="fg.error" fontSize="xs">
          {error}
        </Text>
      ))}

      <FileUpload.List showSize clearable />
    </FileUpload.Root>
  );
}
