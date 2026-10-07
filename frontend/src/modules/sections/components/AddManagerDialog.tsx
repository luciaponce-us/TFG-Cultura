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
import { useAddManagerToSection, useSections } from "../hooks";
import { useAuth } from "@/modules/core/context/useAuth";

interface AddManagerDialogProps {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  sectionId: string;
  managers: string[];
}

export function AddManagerDialog({
  isOpen,
  setIsOpen,
  sectionId,
  managers,
}: AddManagerDialogProps) {
  const { token } = useAuth();
  const { data: sections } = useSections();
  const [error, setError] = useState<string>("");
  const { mutateAsync: addManager, isPending } =
    useAddManagerToSection(setError);
  const [search, setSearch] = useState("");
  const [selectedUsername, setSelectedUsername] = useState<string | null>(null);

  const {
    data: paginatedUsers,
    isLoading,
    isError,
  } = useUsers(token, 0, {
    name: search,
    role: "ENCARGADO",
    active: "",
  });
  const currentSection = sections?.find((section) => section.id === sectionId);
  const currentManagerUsernames = new Set(
    currentSection?.managers.map((manager) => manager.username),
  );
  const availableManagers =
    paginatedUsers?.content.filter(
      (user) =>
        !currentManagerUsernames.has(user.username) &&
        !managers.includes(user.username),
    ) ?? [];
  const selectedManager = availableManagers.find(
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
      title="Agregar encargado"
      submitButtonText="Agregar"
      disabled={!token || !selectedUsername || isPending}
      resetForm={resetForm}
      handleSubmit={async () => {
        if (!token || !selectedUsername) return;

        await addManager({
          token,
          sectionId,
          managerUsername: selectedUsername,
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

      {!isLoading && !isError && availableManagers.length === 0 && (
        <TextSecondary>
          {search
            ? "No se encontraron encargados."
            : "No hay encargados disponibles para añadir."}
        </TextSecondary>
      )}

      {!isLoading && !isError && availableManagers.length > 0 && (
        <VStack align="stretch" gap={2}>
          {availableManagers.map((manager) => {
            const isSelected = manager.username === selectedUsername;

            return (
              <CustomButton
                key={manager.username}
                type="button"
                color={isSelected ? "principal" : "transparent"}
                onClick={() => setSelectedUsername(manager.username)}
                disabled={isPending}
              >
                <HStack w="100%" textAlign="left">
                  <CustomAvatar
                    src={manager.avatar}
                    name={manager.username}
                    size="sm"
                  />
                  <VStack align="start" gap={0}>
                    <Text>
                      {manager.name} {manager.surname}
                    </Text>
                    <Text fontSize="sm" opacity={0.8}>
                      @{manager.username}
                    </Text>
                  </VStack>
                </HStack>
              </CustomButton>
            );
          })}
        </VStack>
      )}

      {selectedManager && (
        <Text fontSize="sm" color="principal.800">
          Encargado seleccionado: @{selectedManager.username}
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
