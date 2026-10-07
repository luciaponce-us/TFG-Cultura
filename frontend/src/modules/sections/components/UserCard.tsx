import { CustomAvatar, CustomButton } from "@/modules/core/components";
import type { User } from "@/modules/users/types";
import { HStack, Text, VStack } from "@chakra-ui/react";
import { IconX } from "@tabler/icons-react";
import type { MouseEvent } from "react";
import { useNavigate } from "react-router-dom";
import { useAuth } from "@/modules/core/context/useAuth";
import { useRemoveCollaboratorFromSection } from "../hooks/useRemoveCollaboratorFromSection";
import { useRemoveManagerFromSection } from "../hooks/useRemoveManagerFromSection";

export function UserCard({
  user,
  sectionId,
  isSuperAdmin,
}: {
  user: User;
  sectionId: string;
  isSuperAdmin: boolean;
}) {
  const { token } = useAuth();
  const { mutateAsync: removeManager, isPending: isDeletingManager } =
    useRemoveManagerFromSection();
  const { mutateAsync: removeCollaborator, isPending: isDeletingCollaborator } =
    useRemoveCollaboratorFromSection();
  const isDeleting = isDeletingManager || isDeletingCollaborator;

  function handleRemove(e: MouseEvent<HTMLButtonElement>) {
    if (!token || !isSuperAdmin) return;
    e.stopPropagation();

    if (user.role === "ENCARGADO") {
      void removeManager({
        sectionId,
        managerUsername: user.username,
      });
    }

    if (user.role === "COLABORADOR") {
      void removeCollaborator({
        sectionId,
        collaboratorUsername: user.username,
      });
    }
  }

  const navigate = useNavigate();
  return (
    <HStack
      w="100%"
      minW={0}
      justify="top"
      alignItems="center"
      borderWidth={1}
      borderRadius="md"
      p={4}
      gap={2}
      key={user.username}
      onClick={() => {
        if (!isDeleting) {
          void navigate(`/admin/usuarios/${user.username}`);
        }
      }}
      _hover={{
        cursor: isDeleting ? "not-allowed" : undefined,
        transform: "scale(1.02)",
        boxShadow: "md",
      }}
      _active={{
        transform: isDeleting ? "none" : "!important scale(0.99)",
        bg: isDeleting ? "none" : "gray.100",
        boxShadow: isDeleting ? "none" : "sm",
      }}
      filter={isDeleting ? "grayscale(100%)" : "none"}
      opacity={isDeleting ? 0.5 : 1}
      h="100%"
      minH={0}
      overflow="hidden"
      flex="1"
    >
      <CustomAvatar src={user.avatar} name={user.username} size="sm" />
      <VStack gap={0} w="100%" minW={0} align="start" justify="center">
        <Text fontWeight="bold">
          {user.name} {user.surname}
        </Text>
        <Text color="gray.500">@{user.username}</Text>
      </VStack>
      {isSuperAdmin && (
      <CustomButton
        color="transparent"
        onClick={handleRemove}
        loading={isDeleting}
        disabled={isDeleting || !token}
      >
        <IconX />
      </CustomButton>
      )}
    </HStack>
  );
}
