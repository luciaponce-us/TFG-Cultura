import { Dialog, Heading, Portal, Text } from "@chakra-ui/react";
import { useState } from "react";
import { CustomButton } from "./CustomButton";

interface ConfirmDialogProps {
  isOpen: boolean;
  setIsOpen: (open: boolean) => void;
  handleAction: () => void;
  title: string;
  message: string;
}

export function ConfirmDialog({
  isOpen,
  setIsOpen,
  handleAction,
  title,
  message,
}: ConfirmDialogProps) {
  const [loading, setLoading] = useState(false);

  function handleConfirm() {
    setLoading(true);
    try {
      handleAction();
    } catch (error) {
      console.error("Error al eliminar:", error);
    } finally {
      setIsOpen(false);
      setLoading(false);
    }
  }

  return (
    <Dialog.Root open={isOpen} onOpenChange={(e) => setIsOpen(e.open)} modal>
      <Portal>
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
              <Text
                whiteSpace="normal"
                overflowWrap="break-word"
                textAlign="left"
              >
                {message}
              </Text>
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
      </Portal>
    </Dialog.Root>
  );
}
