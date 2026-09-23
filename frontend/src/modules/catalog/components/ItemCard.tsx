import { Heading, HStack, Image, Text, VStack } from "@chakra-ui/react";
import type { CreateItemDialogProps, Item, ItemType } from "../types";
import { useAuth } from "@/modules/core/context/useAuth";
import { ConfirmDialog, CustomButton } from "@/modules/core/components";
import { IconPencil, IconTrash } from "@tabler/icons-react";
import { useState } from "react";
import { useDeleteItem } from "../hooks";
import { PLACEHOLDER } from "@/modules/core/utils/utils";

interface ItemCardProps<T extends Item> {
  item: T;
  type: ItemType;
  CreateItemDialog: React.ComponentType<CreateItemDialogProps>;
  sagaId?: string;
}

export function ItemCard<T extends Item>({
  item,
  type,
  CreateItemDialog,
  sagaId,
}: ItemCardProps<T>) {
  const { isAdmin } = useAuth();
  const [isEditOpen, setIsEditOpen] = useState(false);
  const { mutateAsync: deleteItem, isPending: isDeleting } = useDeleteItem(
    item.id,
    type,
  );
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  return (
    <>
      <HStack
        w="100%"
        justifyContent="space-between"
        alignItems="center"
        borderWidth={1}
        borderRadius="md"
        p={4}
        gap={6}
      >
        <HStack gap={4} alignItems="center">
          <Image
            src={item.imageUrl ?? PLACEHOLDER.ROLSAGA}
            alt={item.name}
            width="100px"
            height="auto"
            borderRadius="sm"
            aspectRatio="2/3"
          />
          <VStack align="start" gap={1} justify="top" maxW="250px">
            <Heading as="h2" size="md">
              {" "}
              {item.name}{" "}
            </Heading>
            <Text>{item.description}</Text>
          </VStack>
        </HStack>
        {isAdmin && (
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
        )}
      </HStack>
      {isEditOpen && (
        <CreateItemDialog
          isOpen
          setIsOpen={setIsEditOpen}
          itemId={item.id}
          sagaId={sagaId}
        />
      )}
      {isDeleteDialogOpen && (
        <ConfirmDialog
          isOpen
          setIsOpen={setIsDeleteDialogOpen}
          title="Confirmar eliminación"
          message={`¿Estás seguro de que quieres eliminar "${item.name}"? Esta acción no se puede deshacer.`}
          handleAction={() => void deleteItem()}
        />
      )}
    </>
  );
}
