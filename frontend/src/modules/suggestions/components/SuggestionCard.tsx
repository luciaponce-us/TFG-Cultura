import { VStack, HStack, Text } from "@chakra-ui/react";
import type { Suggestion, SuggestionType } from "../types";
import { MANAGEMENT_ROLES, type User } from "@/modules/users/types";
import {
  CustomAvatar,
  CustomAvatarGroup,
  CustomButton,
} from "@/modules/core/components";
import { IconThumbUp } from "@tabler/icons-react";
import { parseRole } from "@/modules/users/utils";

export function SuggestionCard({ suggestion }: { suggestion: Suggestion }) {
  function parseType(type: SuggestionType): string {
    switch (type) {
      case "CATALOG":
        return "#catálogo";
      case "EVENT":
        return "#eventos";
      case "OTHER":
        return "#otro";
      default:
        return "#" + type;
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
      <HStack gap={4} mb={2} align="start">
        <CustomAvatar
          src={suggestion.author.avatar}
          name={suggestion.author.name}
          w="80px"
          h="80px"
        />
        <VStack align="start" gap={0}>
          <Text fontSize="lg" fontWeight="bold">
            {suggestion.title}
          </Text>
          <Text fontSize="sm" color="principal.500">
            {parseType(suggestion.type)}
          </Text>
          <Text fontSize="sm" color="gray.600">
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
          <Text fontSize="sm" color="gray.600">
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
        <CustomButton
          onClick={() => alert("Funcionalidad de apoyo no implementada")}
        >
          <IconThumbUp /> Apoyar sugerencia
        </CustomButton>
      </HStack>
    </VStack>
  );
}
