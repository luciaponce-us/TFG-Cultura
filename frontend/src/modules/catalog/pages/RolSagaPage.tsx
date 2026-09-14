import { useParams } from "react-router-dom";
import { useRolGamesBySaga, useRolSaga } from "../hooks";
import { Flex, Heading } from "@chakra-ui/react";

export function RolSagaPage() {
  const { sagaId } = useParams<{ sagaId: string }>();
  const { data: rolSaga, isLoading: isRolSagaLoading } = useRolSaga(sagaId!);
  const { data: rolGames, isLoading } = useRolGamesBySaga(sagaId!);

  if (isLoading || isRolSagaLoading || !rolSaga) {
    return <div>Cargando...</div>;
  }

  return (
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
  );
}
