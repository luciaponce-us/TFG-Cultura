import { useMutation, useQueryClient } from "@tanstack/react-query";

import { deleteSection } from "../service/section.service";
import { useAuth } from "@/modules/core/context/useAuth";
import { toaster } from "@/modules/core/components";

interface DeleteSectionParams {
  sectionId: string;
}

export function useDeleteSection() {
  const queryClient = useQueryClient();
  const { token } = useAuth();

  return useMutation<void, Error, DeleteSectionParams>({
    mutationFn: async ({ sectionId }: DeleteSectionParams) => {
      if (!token) {
        throw new Error("Necesitas iniciar sesión para modificar la sección.");
      }

      return deleteSection(token, sectionId);
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: ["sections"],
      });
    },
    onError: (error: Error) => {
      console.error("Error al eliminar la sección:", error);
      toaster.create({
        title: "Error al eliminar la sección",
        description: error.message,
        type: "error",
      });
    },
  });
}
