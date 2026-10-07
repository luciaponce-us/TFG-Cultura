import { useMutation, useQueryClient } from "@tanstack/react-query";

import { removeManagerFromSection } from "../service/section.service";
import { useAuth } from "@/modules/core/context/useAuth";
import type { Section } from "../types";

interface RemoveManagerFromSectionParams {
  sectionId: string;
  managerUsername: string;
}

export function useRemoveManagerFromSection() {
  const queryClient = useQueryClient();
  const { token } = useAuth();

  return useMutation<Section, Error, RemoveManagerFromSectionParams>({
    mutationFn: async ({
      sectionId,
      managerUsername,
    }: RemoveManagerFromSectionParams) => {
      if (!token) {
        throw new Error("Necesitas iniciar sesión para modificar la sección.");
      }

      return removeManagerFromSection(token, sectionId, managerUsername);
    },

    onSuccess: async () => {
      await queryClient.invalidateQueries({
        queryKey: ["sections"],
      });
    },
  });
}
