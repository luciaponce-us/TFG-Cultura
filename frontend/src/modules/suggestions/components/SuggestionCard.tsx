import { HStack, Text, VStack } from "@chakra-ui/react";
import { IconThumbDown, IconThumbUp, IconTrash } from "@tabler/icons-react";
import { useQueryClient } from "@tanstack/react-query";
import { useState } from "react";
import { useNavigate } from "react-router-dom";

import {
  ConfirmDialog,
  CustomAvatar,
  CustomAvatarGroup,
  CustomButton,
  toaster,
} from "@/modules/core/components";
import { useAuth } from "@/modules/core/context/useAuth";
import { isApiError, useIsMobile } from "@/modules/core/utils/utils";
import { MANAGEMENT_ROLES, type User } from "@/modules/users/types";
import { parseRole } from "@/modules/users/utils";

import { useDeleteSuggestion } from "../hooks";
import { toggleSupportSuggestion } from "../service/suggestion.service";

import type { Suggestion, SuggestionType } from "../types";

export function SuggestionCard({ suggestion }: { suggestion: Suggestion }) {
  const { token, user, isAdmin } = useAuth();
  const isMobile = useIsMobile();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const { mutateAsync: deleteSuggestion, isPending: isDeleting } =
    useDeleteSuggestion();
  const [loadingSupport, setLoadingSupport] = useState(false);
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

  function parseType(type: SuggestionType): string {
    switch (type) {
      case "CATALOG":
        return "#catálogo";
      case "EVENT":
        return "#eventos";
      case "OTHER":
        return "#otro";
    }
  }

  function getImportantSupporters(): User[] {
    const supporters: User[] = suggestion.supporters;
    const importantSupporters = supporters
      .filter((supporter) => MANAGEMENT_ROLES.includes(supporter.role))
      .sort(
        (a, b) =>
          MANAGEMENT_ROLES.indexOf(a.role) - MANAGEMENT_ROLES.indexOf(b.role),
      ) // Jerarquiza por rol
      .slice(0, 3); // Limita a los 3 más importantes
    return importantSupporters;
  }

  function parseUserAvatar(user: User): { src: string; name: string } {
    return {
      src: user.avatar || "",
      name: user.username,
    };
  }

  const importantSupporters = getImportantSupporters();

  function formatSupporterList(supporters: User[]): string {
    const items = supporters.map(
      (supporter) => `${supporter.name} (${parseRole(supporter.role)})`,
    );
    if (items.length <= 1) {
      return items.join("");
    }
    if (items.length === 2) {
      return `${items[0]} y ${items[1]}`;
    }
    return `${items.slice(0, -1).join(", ")} y ${items[items.length - 1]}`;
  }

  const isSupportedByUser = suggestion.supporters.some(
    (supporter) => supporter.username === user?.username,
  );
  const isAuthor = suggestion.author.username === user?.username;

  async function handleToggleSupport() {
    console.log("Toggling support for suggestion:", suggestion.id);
    setLoadingSupport(true);
    if (!token) {
      toaster.create({
        title: "Inicia sesión para apoyar o dejar de apoyar sugerencias",
        description: "Serás redirigido a la página de inicio de sesión",
        closable: true,
      });
      void navigate("/iniciar-sesion");
      setLoadingSupport(false);
    } else {
      try {
        await toggleSupportSuggestion(token, suggestion.id);
        await queryClient.invalidateQueries({
          queryKey: ["suggestions"],
        });
      } catch (error) {
        if (isApiError(error)) {
          console.error(
            "Error toggling support for suggestion:",
            error.message,
          );
          toaster.create({
            title: "Error al apoyar o dejar de apoyar sugerencia",
            description:
              "Ocurrió un error al apoyar o dejar de apoyar la sugerencia. Inténtalo de nuevo.",
            type: "error",
          });
        } else {
          console.error("Unexpected error:", error);
          toaster.create({
            title: "Error inesperado",
            description: "Ocurrió un error inesperado. Inténtalo de nuevo.",
            type: "error",
          });
        }
      }
      setLoadingSupport(false);
    }
  }

  return (
    <VStack
      borderWidth="1px"
      borderRadius="lg"
      p={4}
      bg="white"
      shadow="md"
      align="start"
      gap={4}
    >
      <HStack gap={4} mb={2} align="start" minW={0} maxW="100%" w="100%">
        <CustomAvatar
          src={suggestion.author.avatar}
          name={suggestion.author.name}
          w="80px"
          h="80px"
        />
        <VStack align="start" gap={0} minW={0} maxW="100%" flex={1}>
          <Text fontSize="lg" fontWeight="bold" overflowWrap="break-word" maxW="100%">
            {suggestion.title}
          </Text>
          <Text fontSize="sm" color="principal.500">
            {parseType(suggestion.type)} · Propuesta por @
            {suggestion.author.username}
          </Text>
          <Text fontSize="sm" color="gray.600" minW={0} maxW="100%" overflowWrap="break-word">
            {suggestion.description}
          </Text>
        </VStack>
      </HStack>
      {importantSupporters.length > 0 && (
        <HStack gap={2} align="center">
          <CustomAvatarGroup
            items={importantSupporters.map(parseUserAvatar)}
            max={3}
          />
          <Text fontSize="sm" color="gray.600" minW={0} maxW="100%" overflowWrap="break-word">
            {formatSupporterList(importantSupporters)}{" "}
            {importantSupporters.length === 1 ? "apoya" : "apoyan"} esta
            sugerencia.
          </Text>
        </HStack>
      )}
      <HStack gap={2} align="flex-end" justify="space-between" w="100%">
        <VStack align="start" gap={2}>
          <Text fontSize="sm" color="gray.600">
            {suggestion.totalSupporters > 0
              ? "Apoyada por..."
              : "¡Sé el primero en apoyar esta sugerencia!"}
          </Text>
          <CustomAvatarGroup
            items={suggestion.supporters.map(parseUserAvatar)}
            max={3}
          />
        </VStack>
        <HStack gap={2} align="center">
          {(isAuthor || isAdmin) && (
            <CustomButton
              onClick={() => setDeleteDialogOpen(true)}
              color="rojo"
              loading={isDeleting}
            >
              <IconTrash /> {isMobile ? "" : "Eliminar"}
            </CustomButton>
          )}
          {!isAuthor &&
            (isSupportedByUser ? (
              <CustomButton
                onClick={() => void handleToggleSupport()}
                color="rojo"
              >
                <IconThumbDown /> {isMobile ? "" : "Dejar de apoyar"}
              </CustomButton>
            ) : (
              <CustomButton
                onClick={() => void handleToggleSupport()}
                loading={loadingSupport}
              >
                <IconThumbUp /> {isMobile ? "" : "Apoyar sugerencia"}
              </CustomButton>
            ))}
        </HStack>
      </HStack>
      <ConfirmDialog
        isOpen={deleteDialogOpen}
        setIsOpen={setDeleteDialogOpen}
        handleAction={() => {
          void deleteSuggestion({ suggestionId: suggestion.id });
        }}
        title="Eliminar sugerencia"
        message="¿Estás seguro de que quieres eliminar esta sugerencia? Esta acción es irreversible."
      />
    </VStack>
  );
}
