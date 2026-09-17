import { useAuth } from "@/modules/core/context/useAuth";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toaster } from "@/modules/core/components";
import { updateBook} from "../service/book.service";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";
import type { BookRequest } from "../types/book";

export function useUpdateBook(
  id: string | undefined,
  request: BookRequest,
  image: File | null,
  setErrors: (errors: Record<string, string>) => void,
  setIsOpen: (isOpen: boolean) => void,
) {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      if (!id) {
        toaster.create({
          title: "Libro no válido",
          description:
            "No se proporcionó un ID de libro válido para actualizar.",
          type: "error",
        });
        return;
      }
      if (!token) {
        toaster.create({
          title: "Inicia sesión para actualizar libros",
          description:
            "Necesitas iniciar sesión para actualizar un libro.",
          type: "error",
        });
        return;
      }

      return updateBook(token, id, request, image);
    },
    onSuccess: async (book) => {
      toaster.create({
        title: "Libro actualizado",
        description: "El libro se ha actualizado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["books"] });
      setIsOpen(false);
      return book;
    },
    onError: (error) => {
      console.error("Error al actualizar libro:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setErrors(error.errors);
          toaster.create({
            title: "Error al actualizar libro",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al actualizar libro",
          description: error.message,
          type: "error",
        });
        return;
      }

      toaster.create({
        title: "Error al actualizar libro",
        description:
          "Ocurrió un error inesperado al actualizar el libro. Por favor, inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}
