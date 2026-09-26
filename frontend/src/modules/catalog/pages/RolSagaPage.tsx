import { useNavigate, useParams } from "react-router-dom";
import { useDeleteRolSaga, useRolSaga } from "../hooks";
import {
  Grid,
  Heading,
  HStack,
  Separator,
  Spinner,
  VStack,
  Text,
  Box,
  Link,
} from "@chakra-ui/react";

import { ItemDescription, RolGamesGrid } from "../components";
import { useAuth } from "@/modules/core/context/useAuth";
import { ItemImage } from "../components/ItemImage";
import { CategoryTag } from "@/modules/categories/components/CategoryTag";
import type { RolSaga } from "../types/rolgame";
import {
  IconCrown,
  IconDice5,
  IconFile,
  IconPencil,
  IconTrash,
  IconUsers,
  IconWorld,
} from "@tabler/icons-react";
import { COLORS } from "@/styles/theme";
import { parseGameMaster } from "../utils/rol.utils";
import { CustomButton } from "@/modules/core/components/CustomButton";
import { useState } from "react";
import { ConfirmDialog } from "@/modules/core/components";
import { CreateRolSagaDialog } from "../components/forms/CreateRolSagaDialog";

export function RolSagaPage() {
  const { sagaId } = useParams<{ sagaId: string }>();
  const { isAdmin } = useAuth();
  const navigate = useNavigate();
  const { mutateAsync: deleteRolSaga, isPending: isDeleting } =
    useDeleteRolSaga(sagaId, () => void navigate(-1));
  const { data: rolSaga, isLoading } = useRolSaga(sagaId, isDeleting);
  const [isEditOpen, setIsEditOpen] = useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);

  let content;

  if (isLoading || !rolSaga) {
    content = <Spinner size="xl" color="principal.500" />;
  } else {
    content = (
      <>
        <Grid
          w="100%"
          minH={0}
          alignItems={{ base: "stretch", md: "start" }}
          templateColumns={{ base: "1fr", md: "0.5fr 1fr" }}
          gap={6}
          h="fit-content"
        >
          <ItemImage item={rolSaga} />

          <VStack
            w="100%"
            justify="space-between"
            h="100%"
            minW={0}
            py={4}
            gap={6}
          >
            <VStack w="100%" align="start" gap={4}>
              <HStack w="100%" justify="space-between" align="center">
                <VStack align="start" gap={1}>
                  <Heading as="h1" wordBreak="break-word">
                    {rolSaga.name}
                  </Heading>

                  <Text
                    fontSize="sm"
                    color="gray.500"
                    wordBreak="break-all"
                    lineClamp={1}
                  >
                    Saga de rol
                  </Text>
                </VStack>
                {isAdmin && (
                  <HStack>
                    <CustomButton
                      onClick={() => {
                        setIsEditOpen(true);
                      }}
                    >
                      <IconPencil />
                    </CustomButton>
                    <CustomButton
                      color="rojo"
                      onClick={() => {
                        setIsDeleteDialogOpen(true);
                      }}
                      loading={isDeleting}
                    >
                      <IconTrash />
                    </CustomButton>
                  </HStack>
                )}
              </HStack>

              <ItemDescription description={rolSaga.description} />
              <Box
                display="flex"
                flexWrap="wrap"
                gap={1}
                w="100%"
                justifyContent="start"
              >
                {rolSaga.categories.length > 0 &&
                  rolSaga.categories.map((category) => (
                    <CategoryTag key={category.id} category={category} />
                  ))}
              </Box>
              <Separator w="100%" />
              <ExtraInfo rolSaga={rolSaga} />
            </VStack>
          </VStack>
        </Grid>
        {isEditOpen && (
          <CreateRolSagaDialog
            isOpen
            setIsOpen={setIsEditOpen}
            rolSagaId={rolSaga.id}
          />
        )}
        {isDeleteDialogOpen && (
          <ConfirmDialog
            isOpen
            setIsOpen={setIsDeleteDialogOpen}
            title="Confirmar eliminación"
            message={`¿Estás seguro de que quieres eliminar "${rolSaga.name}"? Esta acción no se puede deshacer.`}
            handleAction={() => void deleteRolSaga()}
          />
        )}
      </>
    );
  }

  return (
    <Grid
      templateColumns={{ base: "1fr", lg: "1fr 0.5fr" }}
      gap={10}
      maxW={{ base: "80vw", md: "80vw" }}
    >
      <VStack
        bg="background"
        borderRadius="xl"
        boxShadow="lg"
        p={6}
        align="stretch"
        justify="flex-start"
        h="fit-content"
        gap={6}
      >
        {content}
      </VStack>
      <VStack
        bg="background"
        borderRadius="xl"
        boxShadow="lg"
        p={6}
        align="stretch"
        justify="flex-start"
        h="fit-content"
        gap={6}
      >
        <RolGamesGrid sagaId={rolSaga?.id} />
      </VStack>
    </Grid>
  );
}

function ExtraInfo({ rolSaga }: { rolSaga: RolSaga }) {
  return (
    <VStack w="100%" align="start" gap={4}>
      <Heading as="h2" size="md">
        Más información
      </Heading>
      <VStack w="100%" align="start" gap={2}>
        <HStack>
          <IconWorld color={COLORS.TEXT_HEADER} />
          <Text fontWeight="bold">Sitio web:</Text>
          <Link
            href={rolSaga.website}
            target="_blank"
            rel="noopener noreferrer"
            color="principal.800"
          >
            Ir al sitio web
          </Link>
        </HStack>
        <HStack>
          <IconFile color={COLORS.TEXT_HEADER} />
          <Text fontWeight="bold">Hoja de personaje:</Text>
          <Link
            href={rolSaga.characterSheetUrl}
            target="_blank"
            rel="noopener noreferrer"
            color="principal.800"
          >
            Ver hoja de personaje
          </Link>
        </HStack>
        <HStack>
          <IconCrown color={COLORS.TEXT_HEADER} />
          <Text fontWeight="bold">Director de juego:</Text>
          <Text>{parseGameMaster(rolSaga.gameMaster)}</Text>
        </HStack>
        <HStack>
          <IconDice5 color={COLORS.TEXT_HEADER} />
          <Text fontWeight="bold">Dados utilizados:</Text>
          <Text>{rolSaga.dice}</Text>
        </HStack>
        <HStack>
          <IconUsers color={COLORS.TEXT_HEADER} />
          <Text fontWeight="bold">Jugadores recomendados:</Text>
          <Text>{rolSaga.recommendedPlayers}</Text>
        </HStack>
      </VStack>
    </VStack>
  );
}
