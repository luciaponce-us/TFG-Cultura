import { Flex, Text, Button, VStack, FileUpload, Box } from "@chakra-ui/react";
import { IconCloudUpload } from "@tabler/icons-react";
import { TextSecondary } from "./text";
import { useState } from "react";

type UploadBoxProps = {
  readonly text: React.ReactNode;
  readonly secondaryText: string;
  readonly fileType: FileUpload.FileMimeType;
  readonly required?: boolean;
  readonly onFileChange?: (file: File | null) => void;
  readonly disabled?: boolean;
};

export function UploadBox({
  text,
  secondaryText,
  fileType,
  required = false,
  onFileChange,
  disabled = false,
}: UploadBoxProps) {
  const [errors, setErrors] = useState<string[]>([]);
  const [acceptedFiles, setAcceptedFiles] = useState<File[]>([]);

  function parseErrorMessage(errorType: string) {
    let allowedFileType: string | undefined;
    switch (errorType) {
      case "FILE_TOO_LARGE":
        return "El archivo es demasiado grande. El tamaño máximo permitido es de 2MB.";
      case "FILE_INVALID_TYPE":
        if (fileType == "application/pdf") allowedFileType = "archivos PDF";
        if (fileType == "image/*") allowedFileType = "imágenes";
        return allowedFileType
          ? "El tipo de archivo no es válido."
          : `El tipo de archivo no es válido. Solo se permiten ${allowedFileType}.`;
      case "TOO_MANY_FILES":
        return "Se ha superado el número máximo de archivos permitidos.";
      default:
        return `Archivo rechazado por razones desconocidas: ${errorType}`;
    }
  }

  function getErrorMessage(details: FileUpload.FileRejectDetails) {
    setErrors([]);
    if (details.files.length === 0) {
      console.warn("Ignoring empty FileReject event", details);
      return;
    }
    const errorTypes = details.files[0].errors;
    const newErrors: string[] = [];

    for (const errorType of errorTypes) {
      newErrors.push(parseErrorMessage(errorType));
    }

    setErrors(newErrors);
  }

  function handleAccept(details: FileUpload.FileAcceptDetails) {
    const file = details.files[0] ?? null;

    setErrors([]);
    setAcceptedFiles([file]); // Sustituye el anterior
    onFileChange?.(file);
  }

  return (
    <Box flex={1} minW={0} w="100%">
      <FileUpload.Root
        acceptedFiles={acceptedFiles}
        maxFiles={1}
        allowDrop={true}
        maxFileSize={2 * 1024 * 1024}
        accept={fileType}
        required={required}
        onFileAccept={handleAccept}
        onFileReject={getErrorMessage}
        onFileChange={(details) => {
          setAcceptedFiles(details.acceptedFiles);
          onFileChange?.(details.acceptedFiles[0] ?? null);
        }}
        disabled={disabled}
        w="100%"
      >
        <FileUpload.HiddenInput />
        <FileUpload.Dropzone
          border="2px dashed"
          borderColor={errors.length > 0 ? "fg.error" : "gray.300"}
          borderRadius="xl"
          p={6}
          minH="unset"
          bg="gray.50"
          flexWrap="wrap"
          color="principal.800"
          disableClick={disabled}
          w="100%"
        >
          <FileUpload.DropzoneContent>
            <Flex
              align={{ base: "stretch", md: "center" }}
              justify="space-between"
              direction={{ base: "column", md: "row" }}
              gap={4}
              w="100%"
            >
              <Flex align="center" flex={1} minW={0} gap={4}>
                <IconCloudUpload stroke={1} height="50px" width="50px" />

                <VStack align="start" gap={0} minW={0}>
                  <Text wordBreak="break-word">{text}</Text>
                  <TextSecondary>{secondaryText}</TextSecondary>
                </VStack>
              </Flex>

              <FileUpload.Trigger asChild>
                <Button
                  bg="principal.500"
                  color="white"
                  borderRadius="full"
                  _hover={{ bg: "principal.600" }}
                  disabled={disabled}
                  flexShrink={0}
                  w={{ base: "100%", md: "auto" }}
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

        {acceptedFiles.length > 0 && <FileUpload.List showSize clearable />}
      </FileUpload.Root>
    </Box>
  );
}
