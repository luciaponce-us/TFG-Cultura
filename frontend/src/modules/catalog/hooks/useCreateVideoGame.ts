import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useAuth } from "@/modules/core/context/useAuth";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";
import type { VideoGameRequest } from "../types/videogame";
import { createVideoGame } from "../service/videogame.service";

export function useCreateVideoGame(request: VideoGameRequest, image: File | null, setErrors: (errors: Record<string, string>) => void,
  setIsOpen: (isOpen: boolean) => void) {

      const { token } = useAuth();
      const queryClient = useQueryClient();
    
      return useMutation({
        mutationFn: async () => {
          if (!token) {
            toaster.create({
              title: "Inicia sesión para crear videojuegos",
              description: "Necesitas iniciar sesión para crear un nuevo videojuego.",
              type: "error",
            });
            return;
          }
    
          await createVideoGame(request, image, token);
        },
        onSuccess: async () => {
          toaster.create({
            title: "Videojuego creado",
            description: "El videojuego se ha creado correctamente.",
          });
    
          await queryClient.invalidateQueries({ queryKey: ["videogames"] });
          setIsOpen(false);
        },
        onError: (error) => {
          console.error("Error al crear videojuego:", error);
    
          if (isApiError(error)) {
            if (isFieldError(error)) {
              setErrors(error.errors);
              toaster.create({
                title: "Error al crear videojuego",
                description:
                  "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
                type: "error",
              });
              return;
            }
    
            toaster.create({
              title: "Error al crear videojuego",
              description: error.message,
              type: "error",
            });
            return;
          }
    
          toaster.create({
            title: "Error al crear videojuego",
            description:
              "Ocurrió un error al crear el videojuego. Inténtalo de nuevo más tarde.",
            type: "error",
          });
        },
      });
    }
    