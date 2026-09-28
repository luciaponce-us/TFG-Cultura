import { useAuth } from "@/modules/core/context/useAuth";
import { useMutation, useQueryClient } from "@tanstack/react-query";

import { deleteSeries } from "../service/series.service";
import { deleteVideoGame } from "../service/videogame.service";
import { ITEM_TYPES, type ItemType } from "../types";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { deleteBook } from "../service/book.service";
import { deleteBoardGame } from "../service/boardgame.service";
import { deleteMovie } from "../service/movie.service";
import { deleteRolGame } from "../service/rolgame.service";

export function useDeleteItem(
  id: string | undefined,
  type: ItemType | undefined,
  onDeleteSuccess?: () => void,
) {
  const { token } = useAuth();
  const queryClient = useQueryClient();

  return useMutation({
    mutationFn: async () => {
      if (!token) {
        toaster.create({
          title: "Inicia sesión para eliminar elementos",
          description: "Necesitas iniciar sesión para eliminar un elemento.",
          type: "error",
        });
        return;
      }
      if (!id || !type) return undefined;

      switch (type) {
        case ITEM_TYPES.BOARD_GAME:
          return deleteBoardGame(token, id);
        case ITEM_TYPES.BOOK:
          return deleteBook(token, id);
        case ITEM_TYPES.MOVIE:
          return deleteMovie(token, id);
        case ITEM_TYPES.SERIES:
          return deleteSeries(token, id);
        case ITEM_TYPES.ROL_GAME:
          return deleteRolGame(token, id);
        case ITEM_TYPES.VIDEO_GAME:
          return deleteVideoGame(token, id);
      }
    },

    onSuccess: async () => {
      if (!type || !id) return;
      await queryClient.invalidateQueries({
        queryKey: [toQueryKey(type)],
      });

      toaster.create({
        title: "Elemento eliminado",
        description: "El elemento se ha eliminado correctamente.",
        type: "success",
      });

      onDeleteSuccess?.();
    },
    onError: (error) => {
      console.error("Error al eliminar elemento:", error);
      toaster.create({
        title: "Error al eliminar elemento",
        description:
          "Ocurrió un error al intentar eliminar el elemento. Por favor, inténtalo de nuevo más tarde.",
        type: "error",
      });
    },
  });
}

function toQueryKey(type: ItemType): string {
  switch (type) {
    case ITEM_TYPES.BOARD_GAME:
      return "boardgames";
    case ITEM_TYPES.BOOK:
      return "books";
    case ITEM_TYPES.MOVIE:
      return "movies";
    case ITEM_TYPES.SERIES:
      return "series";
    case ITEM_TYPES.ROL_GAME:
      return "rolgames";
    case ITEM_TYPES.VIDEO_GAME:
      return "videogames";
  }
}
