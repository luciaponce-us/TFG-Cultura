import { useRolSagas } from "../../hooks";
import { CreateRolSagaDialog } from "../../components/forms/CreateRolSagaDialog";
import { ItemsPage } from "./ItemsPage";

function useRolSagasForItems(page: number) {
  return useRolSagas(page);
}

export function RolSagasPage() {
  return (
    <ItemsPage
      getAllHook={useRolSagasForItems}
      title="Juegos de rol"
      loadText="Cargando..."
      errorText={{
        title: "No se pudieron cargar las sagas de rol.",
        description: "No se pudieron cargar las sagas de rol.",
      }}
      emptyText={() => "No hay juegos de rol disponibles."}
      createText="Crear nueva saga de rol"
      CreateDialogComponent={(props) => (
        <CreateRolSagaDialog
          isOpen={props.isOpen}
          setIsOpen={(value) =>
            props.setIsOpen(
              typeof value === "function" ? value(props.isOpen) : value,
            )
          }
          rolSagaId={props.itemId}
        />
      )}
      showFilters={false}
    />
  );
}
