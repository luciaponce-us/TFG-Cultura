import type { FiltersGetAllItems as Filters } from "../types";
import { useBooks } from "../hooks";
import { CreateBookDialog } from "../components";
import { ItemsPage } from "./ItemsPage";
import { useAuth } from "@/modules/core/context/useAuth";
import { useState } from "react";
import { HStack, Text } from "@chakra-ui/react";
import { CustomButton } from "@/modules/core/components";
import { IconPencil } from "@tabler/icons-react";

function useBooksForPage(page: number, filters: Filters) {
  return useBooks(page, filters, ["MANGA", "COMIC"]);
}

export function MangasAndComicsPage() {
  const [editingBookId, setEditingBookId] = useState<string | null>(null);
  const { isAdmin } = useAuth();
  return (
    <ItemsPage
      getAllHook={useBooksForPage}
      title="Mangas y cómics"
      loadText="Cargando mangas y cómics..."
      errorText={{
        title: "Error al cargar los mangas y cómics",
        description:
          "No se pudieron cargar los mangas y cómics. Inténtalo de nuevo más tarde.",
      }}
      emptyText="No hay mangas ni cómics disponibles."
      createText="Crear manga o cómic"
      CreateDialogComponent={CreateBookDialog}
      sectionDefaultValue="Manga"
      renderItem={(book) => (
        <>
          <HStack>
            <Text>
              {`${book.name} - ${book.author != null ? `Autor: ${book.author}` : "Sin autor"}`}
            </Text>
            {isAdmin && (
              <CustomButton onClick={() => setEditingBookId(book.id)}>
                <IconPencil />
              </CustomButton>
            )}
          </HStack>
          {editingBookId === book.id && editingBookId != undefined && (
            <CreateBookDialog
              isOpen
              setIsOpen={(isOpen) => {
                if (!isOpen) {
                  setEditingBookId(null);
                }
              }}
              bookId={book.id}
            />
          )}
        </>
      )}
    />
  );
}
