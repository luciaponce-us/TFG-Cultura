import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useAuth } from "@/modules/core/context/useAuth";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { deleteRolSaga } from "../service/rolsaga.service";

export function useDeleteRolSaga(id: string | undefined) {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
        if (!token) {
            toaster.create({
                title: "Inicia sesión para eliminar sagas de rol",
                description:
                    "Necesitas iniciar sesión para eliminar una saga de rol.",
                type: "error",
            });
            return;
        }
        if (!id) return undefined;

      
          return deleteRolSaga(token, id);
      
    },

    onSuccess: async () => {
      toaster.create({
        title: "Saga de rol eliminada",
        description: "La saga de rol se ha eliminado correctamente.",
        type: "success",
      });
      await queryClient.invalidateQueries({
        queryKey: ["rolsagas"],
      });
    },
    onError: (error) => {
      console.error("Error al eliminar saga de rol:", error);
      toaster.create({
        title: "Error al eliminar saga de rol",
        description:
          "Ocurrió un error al intentar eliminar la saga de rol. Por favor, inténtalo de nuevo más tarde.",
        type: "error",
      });
    }
  });
}
