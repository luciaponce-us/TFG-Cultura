import { Dialog, Heading, Portal } from "@chakra-ui/react";
import { CustomButton } from "./CustomButton";

interface CustomDialogProps {
  isOpen: boolean;
  setIsOpen: (open: boolean) => void;
  title: string;
  children?: React.ReactNode;
}

export function CustomDialog({
  isOpen,
  setIsOpen,
  title,
    children,
}: CustomDialogProps) {

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
              {children}
            </Dialog.Body>
            <Dialog.Footer>
              <CustomButton onClick={() => setIsOpen(false)}>
                Volver
              </CustomButton>
            </Dialog.Footer>
          </Dialog.Content>
        </Dialog.Positioner>
      </Portal>
    </Dialog.Root>
  );
}
