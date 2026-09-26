import { useState } from "react";
import type { RolGame } from "../types/rolgame";
import { useAuth } from "@/modules/core/context/useAuth";
import { useNavigate } from "react-router-dom";
import { useDeleteItem } from "../hooks";
import { ITEM_TYPES } from "../types";
import { Box, HStack, VStack, Text } from "@chakra-ui/react";
import { ItemImage } from "./ItemImage";
import { ConfirmDialog, CustomButton } from "@/modules/core/components";
import { IconPencil, IconTrash } from "@tabler/icons-react";
import { CreateRolGameDialog } from "./forms/CreateRolGameDialog";

export function RolGameCard({ rolgame }: { rolgame: RolGame }) {
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const { isAdmin } = useAuth();
  const navigate = useNavigate();
  const { mutateAsync: deleteRolGame, isPending: isDeleting } = useDeleteItem(
    rolgame.id,
    ITEM_TYPES.ROL_GAME,
  );
  return (
    <>
      <HStack
        w="100%"
        minW={0}
        alignItems="stretch"
        borderWidth={1}
        borderRadius="md"
        p={2}
        gap={2}
        key={rolgame.id}
        onClick={() => {
          if (!isDeleting) {
            void navigate(window.location.pathname + `/rolgame/${rolgame.id}`);
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
        minH={{ base: "136px", md: "176px" }}
        overflow="hidden"
        flex="1"
      >
        <HStack
          alignItems="stretch"
          w="100%"
          minW={0}
          gap={2}
          flex={1}
          minH={0}
        >
          <Box
            w={{ base: "88px", md: "112px" }}
            h={{ base: "120px", md: "160px" }}
            flexShrink={0}
          >
            <ItemImage item={rolgame} type={ITEM_TYPES.ROL_GAME} />
          </Box>

          <VStack align="stretch" justify="center" minW={0} flex={1}>
            <VStack gap={1} align="start" textAlign="start" w="100%" minW={0}>
              <Text
                fontWeight="bold"
                fontSize="16px"
                w="100%"
                wordBreak="break-word"
                overflowWrap="break-word"
                lang="es"
                hyphens="auto"
                lineClamp={2}
              >
                {rolgame.name}
              </Text>
              <Text
                fontSize="14px"
                lineClamp={2}
                w="100%"
                overflowWrap="anywhere"
              >
                {rolgame.description}
              </Text>
            </VStack>
          </VStack>

          {isAdmin && (
            <VStack
              justifyContent="center"
              alignSelf="center"
              gap={2}
              flexShrink={0}
            >
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
            </VStack>
          )}
        </HStack>
      </HStack>
      {isEditOpen && (
        <CreateRolGameDialog
          isOpen
          setIsOpen={setIsEditOpen}
          itemId={rolgame.id}
          sagaId={rolgame.saga.id}
        />
      )}
      {isDeleteDialogOpen && (
        <ConfirmDialog
          isOpen
          setIsOpen={setIsDeleteDialogOpen}
          title="Confirmar eliminación"
          message={`¿Estás seguro de que quieres eliminar "${rolgame.name}"? Esta acción no se puede deshacer.`}
          handleAction={() => void deleteRolGame()}
        />
      )}
    </>
  );
}
