import type { FiltersGetAllItems as Filters } from "../types";
import type { BookType } from "../types/book";
import { useBooks } from "../hooks";
import { CreateBookDialog } from "../components";
import { ItemsPage } from "./ItemsPage";
import { useState } from "react";
import { HStack, Text } from "@chakra-ui/react";
import { CustomButton } from "@/modules/core/components";
import { IconPencil } from "@tabler/icons-react";
import { useAuth } from "@/modules/core/context/useAuth";

const BOOK_TYPES: BookType[] = ["NOVEL", "ENCYCLOPEDIA"];

function useBooksForPage(page: number, filters: Filters) {
  return useBooks(page, filters, BOOK_TYPES);
}

export function BooksPage() {
  const [editingBookId, setEditingBookId] = useState<string | null>(null);
  const { isAdmin } = useAuth();
  return (
    <ItemsPage
      getAllHook={useBooksForPage}
      title="Libros"
      loadText="Cargando libros..."
      errorText={{
        title: "Error al cargar los libros",
        description:
          "No se pudieron cargar los libros. Inténtalo de nuevo más tarde.",
      }}
      emptyText="No hay libros disponibles."
      createText="Crear libro"
      CreateDialogComponent={CreateBookDialog}
      sectionDefaultValue="Libros"
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
