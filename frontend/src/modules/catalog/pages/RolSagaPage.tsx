import { useParams } from "react-router-dom";
import { useRolGamesBySaga, useRolSaga } from "../hooks";
import { Flex, Heading } from "@chakra-ui/react";
import { CustomButton } from "@/modules/core/components";
import { useState } from "react";
import { CreateRolGameDialog } from "../components";
import { IconPlus } from "@tabler/icons-react";

export function RolSagaPage() {
  const { sagaId } = useParams<{ sagaId: string }>();
  const { data: rolSaga, isLoading: isRolSagaLoading } = useRolSaga(sagaId!);
  const { data: rolGames, isLoading } = useRolGamesBySaga(sagaId!);
  const [isCreateRolGameOpen, setIsCreateRolGameOpen] = useState(false);

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
              <Flex
                key={rolGame.id}
                p={4}
                borderRadius="md"
                boxShadow="md"
                bg="white"
                direction="column"
              >
                <Heading as="h2" size="md">
                  {rolGame.name}
                </Heading>
                <p>{rolGame.description}</p>
              </Flex>
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
      />
    </>
  );
}
