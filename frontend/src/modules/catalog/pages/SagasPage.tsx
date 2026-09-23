import { Flex, Heading } from "@chakra-ui/react";
import { CustomButton, TextSecondary } from "@/modules/core/components";
import { useState } from "react";
import { useSagas } from "../hooks";
import { CreateSagaDialog, SagaCard } from "../components";
import { IconPlus } from "@tabler/icons-react";

export function SagasPage() {
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const { data: sagas, isLoading, isError } = useSagas();

  let content;

  if (isLoading) {
    content = <TextSecondary>Cargando...</TextSecondary>;
  } else if (isError) {
    content = <TextSecondary>No se pudieron cargar las sagas.</TextSecondary>;
  } else if (sagas && sagas.length > 0) {
    content =
      sagas && sagas.length > 0 ? (
        <Flex direction="column" gap={4} width="100%">
          {sagas.map((saga) => (
            <SagaCard saga={saga} />
          ))}
        </Flex>
      ) : (
        <TextSecondary>No hay juegos de sagas disponibles.</TextSecondary>
      );
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
        <Heading as="h1">Sagas</Heading>
        <CustomButton onClick={() => setIsCreateDialogOpen(true)}>
          <IconPlus />
          Crear saga
        </CustomButton>
        {content}
      </Flex>
      {isCreateDialogOpen && (
        <CreateSagaDialog
          isOpen={isCreateDialogOpen}
          setIsOpen={setIsCreateDialogOpen}
        />
      )}
    </>
  );
}
