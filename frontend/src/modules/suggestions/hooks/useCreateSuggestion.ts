import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toaster } from "@/modules/core/components";
import { useAuth } from "@/modules/core/context/useAuth";
import { createSuggestion } from "../service/suggestion.service";
import type { SuggestionCreateRequest } from "../types";
import {
  isApiError,
  isDeactivatedUserError,
  throwDeactivatedUserError,
} from "@/modules/core/utils/utils";

export function useCreateSuggestion() {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation<void, Error, SuggestionCreateRequest>({
    mutationFn: async (suggestionData: SuggestionCreateRequest) => {
      if (!token) {
        toaster.create({
          title: "Inicia sesión para crear sugerencias",
          description:
            "Necesitas iniciar sesión para crear una nueva sugerencia.",
          type: "error",
        });
        return;
      }

      await createSuggestion(token, suggestionData);
    },
    onSuccess: async () => {
      toaster.create({
        title: "Sugerencia creada",
        description: "La sugerencia se ha creado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["suggestions"] });
    },
    onError: (error) => {
      console.error("Error al crear sugerencia:", error);
      if (isApiError(error) && isDeactivatedUserError(error)) {
        throwDeactivatedUserError(error);
        return;
      }
      toaster.create({
        title: "Error al crear sugerencia",
        description:
          "Ocurrió un error al crear la sugerencia. Inténtalo de nuevo.",
        type: "error",
      });
    },
  });
}
