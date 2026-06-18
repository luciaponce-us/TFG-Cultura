import { createToaster } from "@chakra-ui/react";

/**
 * Ejemplo de uso:
 * toaster.create({
 *   title: "Sesión cerrada exitosamente",
 *   description: "¡Hasta pronto!",
 *   type: "info",
 *   closable: true,
 * });
 */
export const toaster = createToaster({
  placement: "bottom-end",
  pauseOnPageIdle: true,
});
