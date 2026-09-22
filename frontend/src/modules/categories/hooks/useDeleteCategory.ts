import { toaster } from "@/modules/core/components";
import { useAuth } from "@/modules/core/context/useAuth";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { deleteCategory } from "../service/categories.service";

export function useDeleteCategory(id: string | undefined) {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      if (!id) return undefined;
      if (!token) {
        toaster.create({
          title: "Inicia sesión para eliminar categorías",
          description: "Necesitas iniciar sesión para eliminar una categoría.",
          type: "error",
        });
        return;
      }

      return deleteCategory(id, token);
    },
    onSuccess: async () => {
      toaster.create({
        title: "Categoría eliminada",
        description: "La categoría se ha eliminado correctamente.",
        type: "success",
      });
      await queryClient.invalidateQueries({
        queryKey: ["categories"],
      });
    },
    onError: (error) => {
      console.error("Error al eliminar categoría:", error);
      toaster.create({
        title: "Error al eliminar categoría",
        description:
          "Ocurrió un error al intentar eliminar la categoría. Por favor, inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}
