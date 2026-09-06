import { Dialog, Heading, VStack } from "@chakra-ui/react";
import { CustomButton } from "./CustomButton";
import { useState } from "react";

interface FormDialogProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  title: string;
  children: React.ReactNode;
  handleSubmit: () => Promise<void>;
  submitButtonText: string;
}

export function FormDialog({
  isOpen,
  setIsOpen,
  title,
  children,
  handleSubmit,
  submitButtonText,
}: FormDialogProps) {
  const [loadingSubmit, setLoadingSubmit] = useState(false);
  const onSubmit = async (e: React.SubmitEvent<HTMLElement>) => {
    e.preventDefault();

    try {
      setLoadingSubmit(true);
      await handleSubmit();
    } finally {
      setLoadingSubmit(false);
    }
  };
  return (
    <Dialog.Root open={isOpen}>
      <Dialog.Backdrop />
      <Dialog.Positioner>
        <Dialog.Content
          maxH="80vh"
          overflow="hidden"
          borderRadius="xl"
          bg="background"
          as="form"
          onSubmit={(e) => void onSubmit(e)}
        >
          <Dialog.CloseTrigger />
          <Dialog.Header>
            <Dialog.Title>
              <Heading as="h1">{title}</Heading>
            </Dialog.Title>
          </Dialog.Header>
          <Dialog.Body>
            <VStack
              overflowY="scroll"
              maxH="60vh"
              align="stretch"
              gap={4}
              px={4}
              py={2}
            >
              {children}
            </VStack>
          </Dialog.Body>
          <Dialog.Footer>
            <CustomButton onClick={() => setIsOpen(false)} color="rojo">
              Cancelar
            </CustomButton>
            <CustomButton
              onClick={() => void handleSubmit()}
              loading={loadingSubmit}
              type="submit"
            >
              {submitButtonText}
            </CustomButton>
          </Dialog.Footer>
        </Dialog.Content>
      </Dialog.Positioner>
    </Dialog.Root>
  );
}
