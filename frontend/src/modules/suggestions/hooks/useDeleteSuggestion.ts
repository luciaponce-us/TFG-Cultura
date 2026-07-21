import { useMutation, useQueryClient } from "@tanstack/react-query";

import { toaster } from "@/modules/core/components";
import { useAuth } from "@/modules/core/context/useAuth";

import { deleteSuggestion } from "../service/suggestion.service";
import {
  isApiError,
  isDeactivatedUserError,
  throwDeactivatedUserError,
} from "@/modules/core/utils/utils";

export function useDeleteSuggestion() {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation<void, Error, { suggestionId: string }>({
    mutationFn: async ({ suggestionId }) => {
      if (!token) {
        toaster.create({
          title: "Inicia sesión para eliminar sugerencias",
          description:
            "Necesitas iniciar sesión para eliminar esta sugerencia.",
          type: "error",
        });
        return;
      }

      await deleteSuggestion(token, suggestionId);
    },
    onSuccess: async () => {
      toaster.create({
        title: "Sugerencia eliminada",
        description: "La sugerencia se ha eliminado correctamente.",
      });

      await queryClient.invalidateQueries({ queryKey: ["suggestions"] });
    },
    onError: (error) => {
      console.error("Error al eliminar sugerencia:", error);
      if (isApiError(error) && isDeactivatedUserError(error)) {
        throwDeactivatedUserError(error);
        return;
      }

      toaster.create({
        title: "Error al eliminar sugerencia",
        description:
          "Ocurrió un error al eliminar la sugerencia. Inténtalo de nuevo.",
        type: "error",
      });
    },
  });
}
