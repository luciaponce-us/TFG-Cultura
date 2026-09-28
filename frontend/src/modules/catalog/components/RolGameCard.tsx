import { useState } from "react";
import type { RolBookType, RolGame } from "../types/rolgame";
import { useAuth } from "@/modules/core/context/useAuth";
import { useDeleteItem } from "../hooks";
import { ITEM_TYPES } from "../types";
import { Box, HStack, VStack, Text, Flex } from "@chakra-ui/react";
import { ItemImage } from "./ItemImage";
import { ConfirmDialog, CustomButton, CustomDialog, toaster } from "@/modules/core/components";
import { IconPencil, IconTrash } from "@tabler/icons-react";
import { CreateRolGameDialog } from "./forms/CreateRolGameDialog";
import { AdminItemInfo } from "./AdminItemInfo";

export function RolGameCard({ rolgame }: { rolgame: RolGame }) {
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const { isAdmin } = useAuth();
  const { mutateAsync: deleteRolGame, isPending: isDeleting } = useDeleteItem(
    rolgame.id,
    ITEM_TYPES.ROL_GAME,
  );
  const [isAdminInfoOpen, setIsAdminInfoOpen] = useState(false);
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
          if (!isDeleting && isAdmin) {
            setIsAdminInfoOpen(true);
          }
        }}
        _hover={isAdmin?{
          cursor: isDeleting ? "not-allowed" : "pointer",
          transform: "scale(1.02)",
          boxShadow: "md",
        }:undefined}
        _active={isAdmin?{
          transform: isDeleting ? "none" : "!important scale(0.99)",
          bg: isDeleting ? "none" : "gray.100",
          boxShadow: isDeleting ? "none" : "sm",
        }:undefined}
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

          <VStack align="center" justify="center" minW={0} flex={1} h="100%">
            <VStack gap={2} align="start" textAlign="start" w="100%" minW={0} h="100%" justifyContent="space-between" p={{ base: 1, md: 2 }} pt={{ base: 1, md: 4 }}>
              <VStack gap={1}>
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
              <HStack justifyContent="space-between" w="100%">
              <RolGameTypeTag rolgameType={rolgame.type} />
                          <CustomButton
    onClick={(event) => {
      event.stopPropagation();
      toaster.create({
        title: "Funcionalidad en desarrollo",
        description: "Esta funcionalidad aún no está disponible. Por favor, inténtalo más tarde."
      });
    }}
  >
    Solicitar préstamo
  </CustomButton>
  </HStack>
            </VStack>
          </VStack>

          {isAdmin && (
            <VStack
              justifyContent="center"
              alignSelf="flex-start"
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
      {isAdminInfoOpen && (
        <CustomDialog
          isOpen
          setIsOpen={setIsAdminInfoOpen}
          title="Información del libro de rol"
        >
          <AdminItemInfo
            item={rolgame}
            type={ITEM_TYPES.ROL_GAME}
            isLoading={rolgame === undefined}

            sagaId={rolgame.saga.id}
            CreateItemDialog={({ isOpen, setIsOpen, itemId, sagaId }) =>
              sagaId ? (
                <CreateRolGameDialog
                  isOpen={isOpen}
                  setIsOpen={setIsOpen}
                  itemId={itemId}
                  sagaId={sagaId}
                />
              ) : null
            }
          />
        </CustomDialog>
      )}
    </>
  );
}

function RolGameTypeTag({ rolgameType }: { rolgameType: RolBookType }) {

  const isBasic = rolgameType === "BASIC";
  const typeLabel = rolgameType === "BASIC" ? "BÁSICO" : "EXPANSIÓN";
  return (
    <Flex
          bg={isBasic ? "green.500" : "blue.500"}
          borderRadius="md"
          px={2}
          py={1}
          align="center"
          justify="center"
        >
          <Text
            color={"white"}
            fontSize="12px"
            fontWeight="bold"
          >
            {typeLabel}
          </Text>
        </Flex>
  )
}