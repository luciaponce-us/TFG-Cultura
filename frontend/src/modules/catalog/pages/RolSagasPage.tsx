import { Flex, Heading, HStack, Image, Link, VStack } from "@chakra-ui/react";
import { useRolSagas } from "../hooks";
import { CustomButton, TextSecondary } from "@/modules/core/components";
import { useState } from "react";
import { CreateRolSagaDialog } from "../components/CreateRolSagaDialog";
import { IconPencil, IconPlus } from "@tabler/icons-react";
import { useAuth } from "@/modules/core/context/useAuth";
import type { RolSaga } from "../types/rolgame";
import { PLACEHOLDER } from "@/modules/core/utils/utils";

export function RolSagasPage() {
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);

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
            <RolSagaCard rolSaga={rolSaga} />
          ))}
        </Flex>
      ) : (
        <TextSecondary>No hay juegos de rol disponibles.</TextSecondary>
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
        <Heading as="h1">Juegos de rol</Heading>
        <CustomButton onClick={() => setIsCreateDialogOpen(true)}>
          <IconPlus />
          Crear nueva saga de rol
        </CustomButton>
        {content}
      </Flex>
      <CreateRolSagaDialog
        isOpen={isCreateDialogOpen}
        setIsOpen={setIsCreateDialogOpen}
      />
    </>
  );
}

function RolSagaCard({ rolSaga }: { rolSaga: RolSaga }) {
  const { isAdmin } = useAuth();
  const [isEditOpen, setIsEditOpen] = useState(false);
  return (
    <>
      <HStack
        key={rolSaga.id}
        p={4}
        borderRadius="md"
        boxShadow="md"
        bg="white"
        justify="space-between"
        gap={10}
      >
        <HStack gap={4} align="start">
        <Image
          src={rolSaga.imageUrl ?? PLACEHOLDER.ROLSAGA}
          alt={rolSaga.name}
          width="100px"
          height="auto"
          borderRadius="sm"
          aspectRatio="1/1"
        />
        <VStack align="start" gap={1} justify="top">
        <Heading as="h2" size="md">
          {rolSaga.name}
        </Heading>
        <p>{rolSaga.description}</p>
        <Link href={`/catalogo/rol/${rolSaga.id}`}>Ver detalles</Link>
        </VStack>
        </HStack>
        {isAdmin && (
          <CustomButton
            onClick={() => {
              setIsEditOpen(true);
            }}
          >
            <IconPencil />
          </CustomButton>
        )}
      </HStack>
      {isEditOpen && (
        <CreateRolSagaDialog
          isOpen
          setIsOpen={setIsEditOpen}
          rolSagaId={rolSaga.id}
        />
      )}
    </>
  );
}
