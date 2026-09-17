import { useBoardGames } from "../hooks";
import { CreateBoardGameDialog } from "../components";
import { ItemsPage } from "./ItemsPage";
import { HStack, Text } from "@chakra-ui/react";
import { CustomButton } from "@/modules/core/components";
import { IconPencil } from "@tabler/icons-react";
import { useState } from "react";
import { useAuth } from "@/modules/core/context/useAuth";

export function BoardGamesPage() {
  const [editingBoardGameId, setEditingBoardGameId] = useState<string | null>(null);
  const { isAdmin } = useAuth();
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
            {isAdmin && (
              <CustomButton onClick={() => setEditingBoardGameId(item.id)}>
                <IconPencil />
              </CustomButton>
            )}
          </HStack>
          {editingBoardGameId === item.id && editingBoardGameId != undefined && (
            <CreateBoardGameDialog
              isOpen={true}
              setIsOpen={(isOpen) => {
                if (!isOpen) {
                  setEditingBoardGameId(null);
                }
              }}
              boardGameId={item.id}
            />
          )}
        </>
      )}
    />
  );
}
