import { useParams } from "react-router-dom";
import { useRolGamesBySaga, useRolSaga } from "../hooks";
import { Flex, Heading, Spinner } from "@chakra-ui/react";
import { CustomButton } from "@/modules/core/components";
import { useState } from "react";
import { CreateRolGameDialog, ItemCard } from "../components";
import { IconPlus } from "@tabler/icons-react";
import { ITEM_TYPES } from "../types";

export function RolSagaPage() {
  const { sagaId } = useParams<{ sagaId: string }>();
  const { data: rolSaga, isLoading: isRolSagaLoading } = useRolSaga(sagaId);
  const { data: rolGames, isLoading } = useRolGamesBySaga(sagaId!);
  const [isCreateRolGameOpen, setIsCreateRolGameOpen] = useState(false);

  let content;

  if (isRolSagaLoading || isLoading || !rolSaga) {
    content = <Spinner size="xl" color="principal.500" />;
  } else {
    content = (
      <>
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
              <ItemCard
                key={rolGame.id}
                item={rolGame}
                type={ITEM_TYPES.ROL_GAME}
                CreateItemDialog={(props) =>
                  props.sagaId ? (
                    <CreateRolGameDialog {...props} sagaId={props.sagaId} />
                  ) : null
                }
                sagaId={rolSaga.id}
              />
            ))}
          </Flex>
        ) : (
          <p>No hay juegos de rol disponibles para esta saga.</p>
        )}
        {isCreateRolGameOpen && (
          <CreateRolGameDialog
            isOpen
            setIsOpen={setIsCreateRolGameOpen}
            sagaId={rolSaga.id}
          />
        )}
      </>
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
      {content}
    </Flex>
  );
}
