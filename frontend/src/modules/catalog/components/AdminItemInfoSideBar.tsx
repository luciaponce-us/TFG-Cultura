import {
  ConfirmDialog,
  CustomButton,
  SideBar,
} from "@/modules/core/components";
import type { Item, ItemType } from "../types";
import { Heading, HStack, Spinner, VStack, Text } from "@chakra-ui/react";
import { parseItemCondition, parsePrice } from "../utils/item.utils";
import { parseDate } from "@/modules/core/utils/utils";
import { useState } from "react";
import { IconPencil, IconTrash } from "@tabler/icons-react";
import { useDeleteItem } from "../hooks";

export function AdminItemInfoSideBar<T extends Item>({
  item,
  isLoading,
  type,
  CreateItemDialog,
  sagaId,
}: {
  item: T | undefined;
  isLoading: boolean;
  type: ItemType;
  CreateItemDialog: React.ComponentType<{
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
    itemId?: string;
    sagaId?: string;
  }>;
  sagaId?: string;
}) {
  const [isEditOpen, setIsEditOpen] = useState(false);
  const { mutateAsync: deleteItem, isPending: isDeleting } = useDeleteItem(
    item?.id,
    type,
  );
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);

  return (
    <>
      <SideBar flex={1}>
        <VStack align="center" gap={4} w="100%" minW="210px">
          <Heading as="h1">Administración</Heading>
          {isLoading || !item ? (
            <Spinner />
          ) : (
            <>
              {item && (
                <VStack w="100%" gap={4}>
                  <VStack w="100%" gap={2}>
                    <HStack w="100%">
                      <Text fontWeight="bold">Estado:</Text>
                      <Text>{parseItemCondition(item.condition)}</Text>
                    </HStack>
                    <HStack w="100%" justifyContent="start" alignItems="start">
                      <Text fontWeight="bold">Comentarios:</Text>
                      <Text wordBreak="break-word">
                        {item.comments ? item.comments : "Sin comentarios."}
                      </Text>
                    </HStack>
                    <HStack w="100%">
                      <Text fontWeight="bold">Comprado el día:</Text>
                      <Text>
                        {item.purchasedAt
                          ? parseDate(item.purchasedAt)
                          : "Sin fecha de compra."}
                      </Text>
                    </HStack>
                    <HStack w="100%">
                      <Text fontWeight="bold">Precio de compra:</Text>
                      <Text>
                        {item.price ? parsePrice(item.price) : "Desconocido."}
                      </Text>
                    </HStack>
                    <HStack w="100%">
                      <Text fontWeight="bold">Copias:</Text>
                      <Text>
                        {item.copies} ({item.availableCopies} disponibles)
                      </Text>
                    </HStack>
                    <HStack w="100%">
                      <Text fontWeight="bold">Días de préstamo:</Text>
                      <Text>{item.loanDays} días</Text>
                    </HStack>
                    <HStack w="100%">
                      <Text fontWeight="bold">Sección:</Text>
                      <Text>{item.section.name}</Text>
                    </HStack>
                    <HStack w="100%">
                      <Text fontWeight="bold">Creado el:</Text>
                      <Text>{parseDate(item.createdAt)}</Text>
                    </HStack>
                  </VStack>
                  <HStack
                    w="100%"
                    gap={2}
                    flexShrink={0}
                    justifyContent="center"
                  >
                    <CustomButton
                      onClick={() => {
                        setIsEditOpen(true);
                      }}
                    >
                      <IconPencil />
                      Editar
                    </CustomButton>
                    <CustomButton
                      color="rojo"
                      onClick={() => {
                        setIsDeleteDialogOpen(true);
                      }}
                      loading={isDeleting}
                    >
                      <IconTrash /> Eliminar
                    </CustomButton>
                  </HStack>
                </VStack>
              )}
            </>
          )}
        </VStack>
      </SideBar>
      {isEditOpen && item && (
        <CreateItemDialog
          isOpen
          setIsOpen={setIsEditOpen}
          itemId={item.id}
          sagaId={sagaId}
        />
      )}
      {isDeleteDialogOpen && item && (
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
