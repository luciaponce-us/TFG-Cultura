import { useAuth } from "@/modules/core/context/useAuth";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import type { BookCreateRequest } from "../types";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { createBook } from "../service/book.service";
import {
  isApiError,
  throwDeactivatedUserError,
} from "@/modules/core/utils/utils";
import { isDeactivatedUserError } from "@/modules/core/utils/utils";

export function useCreateBook() {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation<void, Error, BookCreateRequest>({
    mutationFn: async (bookData: BookCreateRequest) => {
      if (!token) {
        toaster.create({
          title: "Inicia sesión para crear libros",
          description: "Necesitas iniciar sesión para crear un nuevo libro.",
          type: "error",
        });
        return;
      }

      await createBook(token, bookData);
    },
    onSuccess: async () => {
      toaster.create({
        title: "Libro creado",
        description: "El libro se ha creado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["books"] });
    },
    onError: (error) => {
      console.error("Error al crear sugerencia:", error);
      if (isApiError(error) && isDeactivatedUserError(error)) {
        throwDeactivatedUserError(error);
        return;
      }
      toaster.create({
        title: "Error al crear libro",
        description:
          "Ocurrió un error al crear el libro. Inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}
