import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useAuth } from "@/modules/core/context/useAuth";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { deleteSaga } from "../service/saga.service";

export function useDeleteSaga(id: string | undefined) {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      if (!token) {
        toaster.create({
          title: "Inicia sesión para eliminar sagas",
          description: "Necesitas iniciar sesión para eliminar una saga.",
          type: "error",
        });
        return;
      }
      if (!id) return undefined;

      return deleteSaga(token, id);
    },

    onSuccess: async () => {
      toaster.create({
        title: "Saga eliminada",
        description: "La saga se ha eliminado correctamente.",
        type: "success",
      });
      await queryClient.invalidateQueries({
        queryKey: ["sagas"],
      });
      await queryClient.invalidateQueries({
        queryKey: ["books"],
      });
      await queryClient.invalidateQueries({
        queryKey: ["movies"],
      });
    },
    onError: (error) => {
      console.error("Error al eliminar saga:", error);
      toaster.create({
        title: "Error al eliminar saga",
        description:
          "Ocurrió un error al intentar eliminar la saga. Por favor, inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}
