import { Flex, Text, VStack, FileUpload, Box, Grid } from "@chakra-ui/react";
import { IconCloudUpload } from "@tabler/icons-react";
import { TextSecondary } from "./text";
import { useState } from "react";
import { CustomButton } from "./CustomButton";

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
    <Box flex={1} minW={0} w="100%" h="100%" maxH="100%">
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
        h="100%"
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
          maxW="100%"
          h="100%"
        >
          <FileUpload.DropzoneContent>
            <Grid
              gridTemplateColumns={{ base: "1fr", md: "0.9fr 1fr" }}
              alignItems="center"
              gap={4}
              w="100%"
              maxW="100%"
            >
              <Flex align="center" minW={0} gap={4}>
                <IconCloudUpload stroke={1} height="50px" width="50px" />

                <VStack align="start" gap={0} minW={0}>
                  <Text wordBreak="break-word">{text}</Text>
                  <TextSecondary>{secondaryText}</TextSecondary>
                </VStack>
              </Flex>

              <FileUpload.Trigger asChild>
                <CustomButton
                  color="principal"
                  disabled={disabled}
                  onClick={() => {}}
                >
                  Seleccionar archivo
                </CustomButton>
              </FileUpload.Trigger>
            </Grid>
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
