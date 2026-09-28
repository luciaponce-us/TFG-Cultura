import { useState } from "react";
import { HStack, Text } from "@chakra-ui/react";
import { IconPencil, IconTrash } from "@tabler/icons-react";

import { useDeleteSaga } from "../hooks";
import { CustomButton, ConfirmDialog } from "@/modules/core/components";
import { CreateSagaDialog } from "./forms/CreateSagaDialog";

import type { Saga } from "../types/saga";

export function SagaCard({ saga }: { saga: Saga }) {
  const [isEditOpen, setIsEditOpen] = useState(false);
  const { mutateAsync: deleteSaga, isPending: isDeleting } = useDeleteSaga(
    saga.id,
  );
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);

  return (
    <>
      <HStack justify="space-between" align="center">
        <Text>{saga.name}</Text>
        <HStack>
          <CustomButton onClick={() => setIsEditOpen(true)}>
            <IconPencil />
          </CustomButton>
          <CustomButton
            color="rojo"
            onClick={() => setIsDeleteDialogOpen(true)}
            loading={isDeleting}
          >
            <IconTrash />
          </CustomButton>
        </HStack>
      </HStack>
      {isEditOpen && (
        <CreateSagaDialog
          isOpen
          setIsOpen={setIsEditOpen}
          sagaToEditName={saga.name}
        />
      )}
      {isDeleteDialogOpen && (
        <ConfirmDialog
          isOpen
          setIsOpen={setIsDeleteDialogOpen}
          handleAction={() => void deleteSaga()}
          title="Eliminar saga"
          message={`¿Estás seguro de que deseas eliminar la saga "${saga.name}"? Esta acción no se puede deshacer y todos los libros y películas asociados pasarán a no tener saga.`}
        />
      )}
    </>
  );
}
