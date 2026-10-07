import {
  CustomAvatar,
  CustomButton,
  CustomSearchBar,
  FormDialog,
  TextSecondary,
} from "@/modules/core/components";
import { useUsers } from "@/modules/users/hooks";
import { HStack, Spinner, Text, VStack } from "@chakra-ui/react";
import { useState } from "react";
import {
    useAddCollaboratorToSection,
  useSections,
} from "../hooks";
import { useAuth } from "@/modules/core/context/useAuth";

interface AddCollaboratorDialogProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  sectionId: string;
  collaborators: string[];
}

export function AddCollaboratorDialog({
  isOpen,
  setIsOpen,
  sectionId,
  collaborators
}: AddCollaboratorDialogProps) {
  const { token } = useAuth();
  const { data: sections } = useSections();
  const [error, setError] = useState<string>("");
  const { mutateAsync: addCollaborator, isPending } = useAddCollaboratorToSection(setError);
  const [search, setSearch] = useState("");
  const [selectedUsername, setSelectedUsername] = useState<string | null>(null);

  const { data: paginatedUsers, isLoading, isError } = useUsers(token, 0, {
    name: search,
    role: "COLABORADOR",
    active: "",
  });
  const currentSection = sections?.find((section) => section.id === sectionId);
  const currentCollaboratorUsernames = new Set(
    currentSection?.collaborators.map((collaborator) => collaborator.username),
  );
  const availableCollaborators =
    paginatedUsers?.content.filter(
      (user) => !currentCollaboratorUsernames.has(user.username) && !collaborators.includes(user.username),
    ) ?? [];
  const selectedCollaborator = availableCollaborators.find(
    (user) => user.username === selectedUsername,
  );

  function resetForm() {
    setSearch("");
    setSelectedUsername(null);
  }

  return (
    <FormDialog
      isOpen={isOpen}
      setIsOpen={setIsOpen}
      title="Agregar colaborador"
      submitButtonText="Agregar"
      disabled={!token || !selectedUsername || isPending}
      resetForm={resetForm}
      handleSubmit={async () => {
        if (!token || !selectedUsername) return;

        await addCollaborator({
          token,
          sectionId,
          collaboratorUsername: selectedUsername,
        });
        resetForm();
        setIsOpen(false);
      }}
    >
      <CustomSearchBar
        placeholder="Buscar encargado por nombre..."
        value={search}
        onChange={(event) => {
          setSearch(event.currentTarget.value);
          setSelectedUsername(null);
        }}
        disabled={isPending}
      />

      {isLoading && (
        <HStack justify="center">
          <Spinner color="principal.800" />
        </HStack>
      )}

      {isError && (
        <TextSecondary>No se pudieron cargar los encargados.</TextSecondary>
      )}

      {!isLoading && !isError && availableCollaborators.length === 0 && (
        <TextSecondary>
          {search
            ? "No se encontraron colaboradores."
            : "No hay colaboradores disponibles para añadir."}
        </TextSecondary>
      )}

      {!isLoading && !isError && availableCollaborators.length > 0 && (
        <VStack align="stretch" gap={2}>
          {availableCollaborators.map((collaborator) => {
            const isSelected = collaborator.username === selectedUsername;

            return (
              <CustomButton
                key={collaborator.username}
                type="button"
                color={isSelected ? "principal" : "transparent"}
                onClick={() => setSelectedUsername(collaborator.username)}
                disabled={isPending}
              >
                <HStack w="100%" textAlign="left">
                  <CustomAvatar
                    src={collaborator.avatar}
                    name={collaborator.username}
                    size="sm"
                  />
                  <VStack align="start" gap={0}>
                    <Text>{collaborator.name} {collaborator.surname}</Text>
                    <Text fontSize="sm" opacity={0.8}>
                      @{collaborator.username}
                    </Text>
                  </VStack>
                </HStack>
              </CustomButton>
            );
          })}
        </VStack>
      )}

      {selectedCollaborator && (
        <Text fontSize="sm" color="principal.800">
          Colaborador seleccionado: @{selectedCollaborator.username}
        </Text>
      )}
      {error && (
        <Text fontSize="sm" color="rojo.500">
          {error}
        </Text>
      )}
    </FormDialog>
  );
}