import { useParams } from "react-router-dom";
import { useRolGamesBySaga, useRolSaga } from "../hooks";
import { Flex, Heading, HStack, VStack } from "@chakra-ui/react";
import { CustomButton } from "@/modules/core/components";
import { useState } from "react";
import { CreateRolGameDialog } from "../components";
import { IconPencil, IconPlus } from "@tabler/icons-react";

export function RolSagaPage() {
  const { sagaId } = useParams<{ sagaId: string }>();
  const { data: rolSaga, isLoading: isRolSagaLoading } = useRolSaga(sagaId!);
  const { data: rolGames, isLoading } = useRolGamesBySaga(sagaId!);
  const [isCreateRolGameOpen, setIsCreateRolGameOpen] = useState(false);
  const [rolGameToEditId, setRolGameToEditId] = useState<string | null>(null);

  if (isLoading || isRolSagaLoading || !rolSaga) {
    return <div>Cargando...</div>;
  }

  return (
    <>
      <Flex
        bg="background"
        borderRadius="xl"
        boxShadow="lg"
        p={6}
        direction="column"
        align="center"
        justify="flex-start"
        gap={6}
      >
        <Heading as="h1">{rolSaga.name}</Heading>
        <CustomButton
          onClick={() => {
            setIsCreateRolGameOpen(true);
          }}
        >
          <IconPlus /> Crear juego de rol
        </CustomButton>
        {rolGames && rolGames.length > 0 ? (
          <Flex direction="column" gap={4} width="100%">
            {rolGames.map((rolGame) => (
              <HStack
                key={rolGame.id}
                p={4}
                borderRadius="md"
                boxShadow="md"
                bg="white"
                justify="space-between"
              >
                <VStack align="stretch">
                  <Heading as="h2" size="md">
                    {rolGame.name}
                  </Heading>
                  <p>{rolGame.description}</p>
                </VStack>
                <CustomButton
                  onClick={() => {
                    setIsCreateRolGameOpen(true);
                    setRolGameToEditId(rolGame.id);
                  }}
                >
                  <IconPencil />
                </CustomButton>
              </HStack>
            ))}
          </Flex>
        ) : (
          <p>No hay juegos de rol disponibles para esta saga.</p>
        )}
      </Flex>
      <CreateRolGameDialog
        isOpen={isCreateRolGameOpen}
        setIsOpen={setIsCreateRolGameOpen}
        sagaId={sagaId!}
        itemId={rolGameToEditId ?? undefined}
      />
    </>
  );
}
