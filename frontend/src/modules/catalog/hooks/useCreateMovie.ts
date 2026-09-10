import { useAuth } from "@/modules/core/context/useAuth";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import type { MovieRequest } from "../types/movie";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { createMovie } from "../service/movie.service";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";

export function useCreateMovie(
  movieData: MovieRequest,
  image: File | null,
  setErrors: (errors: Record<string, string>) => void,
  setIsOpen: (isOpen: boolean) => void,
) {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      if (!token) {
        toaster.create({
          title: "Inicia sesión para crear películas",
          description:
            "Necesitas iniciar sesión para crear una nueva película.",
          type: "error",
        });
        return;
      }

      await createMovie(token, movieData, image);
    },
    onSuccess: async () => {
      toaster.create({
        title: "Película creada",
        description: "La película se ha creado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["movies"] });
      setIsOpen(false);
    },
    onError: (error) => {
      console.error("Error al crear película:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setErrors(error.errors);
          toaster.create({
            title: "Error al crear película",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al crear película",
          description: error.message,
          type: "error",
        });
        return;
      }

      toaster.create({
        title: "Error al crear película",
        description:
          "Ocurrió un error al crear la película. Inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}
