import { useAuth } from "@/modules/core/context/useAuth";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import type { BookCreateRequest } from "../types";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { createBook } from "../service/book.service";
import { isApiError } from "@/modules/core/utils/utils";

export function useCreateBook(
  bookData: BookCreateRequest,
  image: File | null,
  setErrors: (errors: Record<string, string>) => void,
  setIsOpen: (isOpen: boolean) => void,
) {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      console.log("Creando libro...");
      if (!token) {
        toaster.create({
          title: "Inicia sesión para crear libros",
          description: "Necesitas iniciar sesión para crear un nuevo libro.",
          type: "error",
        });
        return;
      }

      await createBook(token, bookData, image);
    },
    onSuccess: async () => {
      toaster.create({
        title: "Libro creado",
        description: "El libro se ha creado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["books"] });
      setIsOpen(false);
    },
    onError: (error) => {
      console.error("Error al crear libro:", error);

      if (isApiError(error)) {
        if (error.errors && Object.keys(error.errors).length > 0) {
          setErrors(error.errors);
          
          toaster.create({
            title: "Error al crear libro",
            description: "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });

          return;
        }
        
        toaster.create({
          title: "Error al crear libro",
          description: error.message,
          type: "error",
        });
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
