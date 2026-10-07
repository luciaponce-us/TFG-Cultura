import { useState } from "react";
import type { CreateItemDialogProps, Item, ItemType } from "../types";
import { useNavigate } from "react-router-dom";
import { useDeleteItem } from "../hooks";
import { HStack, Spinner, VStack, Text } from "@chakra-ui/react";
import { parseItemCondition, parsePrice } from "../utils/item.utils";
import { parseDate } from "@/modules/core/utils/utils";
import { ConfirmDialog, CustomButton } from "@/modules/core/components";
import { IconPencil, IconTrash } from "@tabler/icons-react";
import type { CreateRolGameDialogProps } from "../types/props";

interface AdminItemInfoProps<T extends Item> {
  item: T | undefined;
  isLoading: boolean;
  type: ItemType;
  CreateItemDialog: React.ComponentType<
    CreateItemDialogProps | CreateRolGameDialogProps
  >;
  sagaId?: string;
}

export function AdminItemInfo<T extends Item>({
  item,
  type,
  isLoading,
  sagaId,
  CreateItemDialog,
}: AdminItemInfoProps<T>) {
  const [isEditOpen, setIsEditOpen] = useState(false);
  const navigate = useNavigate();
  function onDeleteSuccess() {
    void navigate(-1);
  }
  const { mutateAsync: deleteItem, isPending: isDeleting } = useDeleteItem(
    item?.id,
    type,
    onDeleteSuccess,
  );
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  return (
    <>
      <VStack align="center" gap={4} w="100%" minW="210px">
        {isLoading || !item ? (
          <Spinner />
        ) : (
          <>
            {item && (
              <VStack w="100%" gap={4}>
                <VStack w="100%" gap={2}>
                  <HStack w="100%">
                    <Text fontWeight="bold">Nombre:</Text>
                    <Text>{item.name}</Text>
                  </HStack>
                  <HStack w="100%">
                    <Text fontWeight="bold">Descripción:</Text>
                    <Text>{item.description}</Text>
                  </HStack>
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
                    <Text>{item.section? item.section.name : "Sin sección"}</Text>
                  </HStack>
                  <HStack w="100%">
                    <Text fontWeight="bold">Creado el:</Text>
                    <Text>{parseDate(item.createdAt)}</Text>
                  </HStack>
                </VStack>
                <HStack w="100%" gap={2} flexShrink={0} justifyContent="center">
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
