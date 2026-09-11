import { useBoardGames } from "../hooks";
import { CreateBoardGameDialog } from "../components";
import { ItemsPage } from "./ItemsPage";
import { Text } from "@chakra-ui/react";

export function BoardGamesPage() {
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
        <Text>
          {item.name}
          {item.isExpansion && ` - Juego base: ${item.baseGame?.name}`}
        </Text>
      )}
    />
  );
}
