import { Box, HStack, Image, Text, VStack } from "@chakra-ui/react";
import {
  getItemTypeUrl,
  type CreateItemDialogProps,
  type Item,
  type ItemType,
} from "../types";
import { useAuth } from "@/modules/core/context/useAuth";
import { ConfirmDialog, CustomButton } from "@/modules/core/components";
import { IconPencil, IconTrash } from "@tabler/icons-react";
import { useState } from "react";
import { useDeleteItem } from "../hooks";
import { PLACEHOLDER } from "@/modules/core/utils/utils";
import { CategoryTag } from "@/modules/categories/components/CategoryTag";
import { useNavigate } from "react-router-dom";

interface ItemCardProps<T extends Item> {
  item: T;
  type: ItemType;
  CreateItemDialog: React.ComponentType<CreateItemDialogProps>;
  sagaId?: string;
  isSagaItem?: boolean;
}

export function ItemCard<T extends Item>({
  item,
  type,
  CreateItemDialog,
  sagaId,
  isSagaItem = false,
}: ItemCardProps<T>) {
  const { isAdmin } = useAuth();
  const [isEditOpen, setIsEditOpen] = useState(false);
  const { mutateAsync: deleteItem, isPending: isDeleting } = useDeleteItem(
    item.id,
    type,
  );
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const description: string =
    "author" in item ? (item.author as string) : item.description;
  const navigate = useNavigate();
  return (
    <>
      <VStack
        w="100%"
        minW={0}
        justify="top"
        alignItems="center"
        borderWidth={1}
        borderRadius="md"
        p={4}
        gap={2}
        key={item.id}
        onClick={() => {
          if (!isDeleting) {
            void navigate(`/catalogo/${getItemTypeUrl(type)}/${item.id}`);
          }
        }}
        _hover={{
          cursor: isDeleting ? "not-allowed" : "pointer",
          transform: "scale(1.02)",
          boxShadow: "md",
        }}
        _active={{
          transform: isDeleting ? "none" : "!important scale(0.99)",
          bg: isDeleting ? "none" : "gray.100",
          boxShadow: isDeleting ? "none" : "sm",
        }}
        filter={isDeleting ? "grayscale(100%)" : "none"}
        opacity={isDeleting ? 0.5 : 1}
        h="100%"
        minH={0}
        overflow="hidden"
        flex="1"
      >
        <VStack
          justifyContent="space-between"
          alignItems="center"
          w="100%"
          minW={0}
          gap={2}
          flex={1}
          h="100%"
          minH={0}
        >
          <Image
            src={item.imageUrl ?? PLACEHOLDER.ROLSAGA}
            alt={item.name}
            minH={0}
            objectFit="contain"
            borderRadius="md"
            aspectRatio="2/3"
          />
          <VStack
            align="space-between"
            justify="space-between"
            w="100%"
            minW={0}
            flexShrink={0}
            flex={1}
          >
            <VStack gap={1} textAlign="center" w="100%" minW={0}>
              <Text
                fontWeight="bold"
                fontSize="16px"
                w="100%"
                wordBreak="break-word"
                overflowWrap="break-word"
                lang="es"
                hyphens="auto"
                lineClamp={isSagaItem ? 1 : 3}
              >
                {item.name}
              </Text>
              {isSagaItem ? null : (
                <Text
                  fontSize="14px"
                  lineClamp={2}
                  w="100%"
                  overflowWrap="anywhere"
                >
                  {description}
                </Text>
              )}
            </VStack>
          </VStack>
          {!isSagaItem && (
            <Box
              display="flex"
              flexWrap="wrap"
              gap={1}
              w="100%"
              minW={0}
              flexShrink={0}
              justifyContent="center"
            >
              {item.categories.slice(0, 5).map((category) => (
                <CategoryTag key={category.id} category={category} />
              ))}
            </Box>
          )}

          {isAdmin && (
            <HStack justifyContent="center" w="100%" gap={2} flexShrink={0}>
              <CustomButton
                onClick={(event) => {
                  event.stopPropagation();
                  setIsEditOpen(true);
                }}
              >
                <IconPencil />
              </CustomButton>
              <CustomButton
                color="rojo"
                onClick={(event) => {
                  event.stopPropagation();
                  setIsDeleteDialogOpen(true);
                }}
                loading={isDeleting}
              >
                <IconTrash />
              </CustomButton>
            </HStack>
          )}
        </VStack>
      </VStack>
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
