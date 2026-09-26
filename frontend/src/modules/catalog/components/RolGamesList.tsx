import { CustomButton, TextSecondary } from "@/modules/core/components";
import { useRolGamesBySaga } from "../hooks";
import { Box, Heading, VStack } from "@chakra-ui/react";
import { CreateRolGameDialog } from "./forms/CreateRolGameDialog";
import { useAuth } from "@/modules/core/context/useAuth";
import { IconPlus } from "@tabler/icons-react";
import { useState } from "react";
import { RolGameCard } from "./RolGameCard";

interface RolGamesGridProps {
  sagaId: string | undefined;
}

export function RolGamesGrid({ sagaId }: RolGamesGridProps) {
  const { isAdmin } = useAuth();
  const { data: rolGames, isLoading, isError } = useRolGamesBySaga(sagaId!);
  const [isCreateRolGameOpen, setIsCreateRolGameOpen] = useState(false);
  const isEmpty = rolGames && rolGames.length === 0;
  const isReady = rolGames && rolGames.length > 0;

  let content;

  if (sagaId === undefined || isLoading || !rolGames) {
    content = (
      <TextSecondary>Cargando libros de rol de esta saga...</TextSecondary>
    );
  } else if (isError) {
    content = (
      <TextSecondary>
        Ha ocurrido un error al cargar los libros de rol de esta saga.
      </TextSecondary>
    );
  } else if (isEmpty) {
    content = (
      <TextSecondary>
        No hay libros de rol disponibles para esta saga.
      </TextSecondary>
    );
  } else if (isReady) {
    content = (
      <Box position="relative" w="100%">
        <VStack
          w="100%"
          maxH={{ base: "60vh", md: "70vh" }}
          overflowY="auto"
          p={2}
          gap={2}
        >
          {rolGames.map((item) => (
            <RolGameCard key={item.id} rolgame={item} />
          ))}
        </VStack>
        <Box
          position="absolute"
          right={0}
          bottom={0}
          left={0}
          h={6}
          bgGradient="to-t"
          gradientFrom="background"
          gradientTo="transparent"
          pointerEvents="none"
        />
      </Box>
    );
  }

  return (
    <>
      <VStack w="100%" gap={4}>
        <VStack gap={2}>
          <Heading as="h2" size="lg">
            Libros de esta saga
          </Heading>
          {isAdmin && (
            <CustomButton
              onClick={() => {
                setIsCreateRolGameOpen(true);
              }}
            >
              <IconPlus /> Crear libro de rol
            </CustomButton>
          )}
        </VStack>
        {content}
      </VStack>
      {isCreateRolGameOpen && sagaId && (
        <CreateRolGameDialog
          isOpen
          setIsOpen={setIsCreateRolGameOpen}
          sagaId={sagaId}
        />
      )}
    </>
  );
}
