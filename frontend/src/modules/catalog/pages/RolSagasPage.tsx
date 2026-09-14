import { Flex, Heading, Link } from "@chakra-ui/react";
import { useRolSagas } from "../hooks";
import { TextSecondary } from "@/modules/core/components";

export function RolSagasPage() {
  const { data: rolSagas, isLoading, isError } = useRolSagas();

  let content;

  if (isLoading) {
    content = <TextSecondary>Cargando...</TextSecondary>;
  } else if (isError) {
    content = (
      <TextSecondary>No se pudieron cargar las sagas de rol.</TextSecondary>
    );
  } else {
    content =
      rolSagas && rolSagas.content.length > 0 ? (
        <Flex direction="column" gap={4} width="100%">
          {rolSagas.content.map((rolSaga) => (
            <Flex
              key={rolSaga.id}
              p={4}
              borderRadius="md"
              boxShadow="md"
              bg="white"
              direction="column"
            >
              <Heading as="h2" size="md">
                {rolSaga.name}
              </Heading>
              <p>{rolSaga.description}</p>
              <Link href={`/catalogo/rol/${rolSaga.id}`}>Ver detalles</Link>
            </Flex>
          ))}
        </Flex>
      ) : (
        <TextSecondary>No hay juegos de rol disponibles.</TextSecondary>
      );
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
      <Heading as="h1">Juegos de rol</Heading>
      {content}
    </Flex>
  );
}
