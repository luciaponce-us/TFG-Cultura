import { useMutation, useQueryClient } from "@tanstack/react-query";
import type { SectionErrors, SectionRequest } from "../types";
import { toaster } from "@/modules/core/components";
import { isApiError, isFieldError } from "@/modules/core/utils/utils";
import type { Dispatch, SetStateAction } from "react";
import { useAuth } from "@/modules/core/context/useAuth";
import { createSection } from "../service/section.service";

export function useCreateSection(
  setErrors: Dispatch<SetStateAction<SectionErrors>>,
  setIsOpen: (isOpen: boolean) => void,
) {
  const queryClient = useQueryClient();
  const { token } = useAuth();

  return useMutation({
    mutationFn: async (section: SectionRequest) => {
      if (!token) {
        toaster.create({
          title: "Inicia sesión para crear secciones",
          description: "Necesitas iniciar sesión para crear una nueva sección.",
          type: "error",
        });
        return;
      }

      return createSection(token, section);
    },
    onSuccess: async () => {
      toaster.create({
        title: "Sección creada",
        description: "La sección se ha creado correctamente.",
        type: "success",
      });
      await queryClient.invalidateQueries({ queryKey: ["sections"] });
      setIsOpen(false);
    },
    onError: (error) => {
      console.error("Error creando sección:", error);

      if (isApiError(error)) {
        if (isFieldError(error)) {
          setErrors(error.errors);
          toaster.create({
            title: "Error al crear sección",
            description:
              "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
            type: "error",
          });
          return;
        }

        toaster.create({
          title: "Error al crear sección",
          description: error.message,
          type: "error",
        });
        return;
      }

      toaster.create({
        title: "Error al crear sección",
        description:
          "Ocurrió un error inesperado al crear la sección. Por favor, inténtalo de nuevo.",
        type: "error",
      });
    },
  });
}
