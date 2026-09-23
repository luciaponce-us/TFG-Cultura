import { Dialog, Heading, Portal, VStack } from "@chakra-ui/react";
import { CustomButton } from "./CustomButton";
import { useState } from "react";
import { COLORS } from "@/styles/theme";

interface FormDialogProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  title: string;
  children: React.ReactNode;
  handleSubmit: () => Promise<void>;
  submitButtonText: string;
  resetForm?: () => void;
  disabled?: boolean;
}

export function FormDialog({
  isOpen,
  setIsOpen,
  title,
  children,
  handleSubmit,
  submitButtonText,
  resetForm,
  disabled = false,
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

  function cancel() {
    resetForm?.();
    setIsOpen(false);
  }

  return (
    <Dialog.Root open={isOpen}>
      <Portal>
        <Dialog.Backdrop />
        <Dialog.Positioner>
          <Dialog.Content
            maxH="80vh"
            display="flex"
            flexDirection="column"
            borderRadius="xl"
            bg={COLORS.BACKGROUND}
            as="form"
            onSubmit={(e) => void onSubmit(e)}
            px={4}
          >
            <Dialog.CloseTrigger />

            <Dialog.Header flexShrink={0}>
              <Dialog.Title>
                <Heading as="h1" overflowWrap="anywhere">
                  {title}
                </Heading>
              </Dialog.Title>
            </Dialog.Header>

            <Dialog.Body flex="1" minH={0} overflowY="auto" position="relative">
              <VStack align="stretch" gap={4} py={2} pb={10}>
                {children}
              </VStack>
            </Dialog.Body>

            <Dialog.Footer
              flexShrink={0}
              position="relative"
              _before={{
                content: '""',
                position: "absolute",
                top: "-32px",
                left: 0,
                right: 0,
                height: "32px",
                background: `linear-gradient(to bottom, transparent, ${COLORS.BACKGROUND})`,
                pointerEvents: "none",
              }}
            >
              <CustomButton onClick={cancel} color="rojo">
                Cancelar
              </CustomButton>

              <CustomButton
                loading={loadingSubmit}
                type="submit"
                disabled={disabled}
              >
                {submitButtonText}
              </CustomButton>
            </Dialog.Footer>
          </Dialog.Content>
        </Dialog.Positioner>
      </Portal>
    </Dialog.Root>
  );
}
