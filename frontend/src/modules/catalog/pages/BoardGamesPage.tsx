import { useBoardGames } from "../hooks";
import { CreateBoardGameDialog } from "../components";
import { ItemsPage } from "./ItemsPage";
import { HStack, Text } from "@chakra-ui/react";
import { CustomButton } from "@/modules/core/components";
import { IconPencil } from "@tabler/icons-react";
import { useState } from "react";

export function BoardGamesPage() {
  const [isEditOpen, setIsEditOpen] = useState(false);
  return (
    <ItemsPage
      getAllHook={useBoardGames}
      title="Juegos de mesa"
      loadText="Cargando juegos de mesa..."
      errorText={{
        title: "Error al cargar los juegos de mesa",
        description:
          "No se pudieron cargar los juegos de mesa. Inténtalo de nuevo más tarde.",
      }}
      emptyText="No hay juegos de mesa disponibles."
      createText="Crear juego de mesa"
      CreateDialogComponent={CreateBoardGameDialog}
      renderItem={(item) => (
        <>
          <HStack>
            <Text>
              {item.name}
              {item.isExpansion && ` - Juego base: ${item.baseGame?.name}`}
            </Text>
            <CustomButton onClick={() => setIsEditOpen(true)}>
              <IconPencil />
            </CustomButton>
          </HStack>
          <CreateBoardGameDialog
            isOpen={isEditOpen}
            setIsOpen={setIsEditOpen}
            boardGameId={item.id}
          />
        </>
      )}
    />
  );
}
