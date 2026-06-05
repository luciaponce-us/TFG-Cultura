import { Dialog, Heading, VStack, Text } from "@chakra-ui/react";
import { useState } from "react";
import { CustomButton } from "./CustomButton";

interface DeleteDialogProps {
  isOpen: boolean;
  setIsOpen: (open: boolean) => void;
  handleDelete: () => void;
  title: string;
  message: string;
}

export function DeleteDialog({
  isOpen,
  setIsOpen,
  handleDelete,
  title,
  message,
}: DeleteDialogProps) {
  const [loading, setLoading] = useState(false);

  function handleConfirm() {
    setLoading(true);
    try {
      handleDelete();
    } catch (error) {
      console.error("Error al eliminar:", error);
    } finally {
      setIsOpen(false);
      setLoading(false);
    }
  }

  return (
    <Dialog.Root open={isOpen} onOpenChange={() => setIsOpen(false)}>
      <Dialog.Backdrop />
      <Dialog.Positioner>
        <Dialog.Content
          maxH="80vh"
          overflow="hidden"
          borderRadius="xl"
          bg="background"
        >
          <Dialog.CloseTrigger />
          <Dialog.Header>
            <Dialog.Title>
              <Heading as="h1">{title}</Heading>
            </Dialog.Title>
          </Dialog.Header>
          <Dialog.Body>
            <VStack>
              <Text>{message}</Text>
            </VStack>
          </Dialog.Body>
          <Dialog.Footer>
            <CustomButton onClick={() => setIsOpen(false)} color="rojo">
              Cancelar
            </CustomButton>
            <CustomButton onClick={handleConfirm} loading={loading}>
              Confirmar
            </CustomButton>
          </Dialog.Footer>
        </Dialog.Content>
      </Dialog.Positioner>
    </Dialog.Root>
  );
}
