import { useMutation, useQueryClient } from "@tanstack/react-query";

import { removeCollaboratorFromSection } from "../service/section.service";
import { useAuth } from "@/modules/core/context/useAuth";
import type { Section } from "../types";

interface RemoveCollaboratorFromSectionParams {
  sectionId: string;
  collaboratorUsername: string;
}

export function useRemoveCollaboratorFromSection() {
  const queryClient = useQueryClient();
  const { token } = useAuth();

  return useMutation<Section, Error, RemoveCollaboratorFromSectionParams>({
    mutationFn: async ({
      sectionId,
      collaboratorUsername,
    }: RemoveCollaboratorFromSectionParams) => {
      if (!token) {
        throw new Error("Necesitas iniciar sesión para modificar la sección.");
      }

      return removeCollaboratorFromSection(
        token,
        sectionId,
        collaboratorUsername,
      );
    },
    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: ["sections"],
      });
    },
  });
}
