import { useAuth } from "@/modules/core/context/useAuth";
import { useState } from "react";
import {
  INITIAL_SECTION_ERRORS,
  INITIAL_SECTION_FORM,
  type SectionErrors,
  type SectionRequest,
} from "../../types";
import type { User } from "@/modules/users/types";
import {
  CustomInput,
  CustomSearchBar,
  CustomSelect,
  FormDialog,
  TextSecondary,
} from "@/modules/core/components";
import { handleChange } from "@/modules/core/utils/utils";
import { MAX_LENGTH, validateSectionForm } from "../../validations";
import { useCreateSection } from "../../hooks";
import { useUsers } from "@/modules/users/hooks";
import { Heading, HStack, Separator, Spinner } from "@chakra-ui/react";

interface SectionFormDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: (isOpen: boolean) => void;
  readonly section?: {
    name: string;
    managers: User[];
    collaborators: User[];
  };
}

export function SectionFormDialog({
  isOpen,
  setIsOpen,
  section,
}: SectionFormDialogProps) {
  const { token } = useAuth();
  const [errors, setErrors] = useState<SectionErrors>(INITIAL_SECTION_ERRORS);
  const [form, setForm] = useState<SectionRequest>(
    section
      ? {
          name: section.name,
          managersUsernames: section.managers.map(
            (manager) => manager.username,
          ),
          collaboratorsUsernames: section.collaborators.map(
            (collaborator) => collaborator.username,
          ),
        }
      : INITIAL_SECTION_FORM,
  );
  const [managerSearch, setManagerSearch] = useState("");
  const [collaboratorSearch, setCollaboratorSearch] = useState("");

  const {
    data: managersData,
    isLoading: isLoadingManagers,
    isError: isManagersError,
  } = useUsers(token, 0, {
    name: managerSearch,
    role: "ENCARGADO",
    active: "",
  });
  const {
    data: collaboratorsData,
    isLoading: isLoadingCollaborators,
    isError: isCollaboratorsError,
  } = useUsers(token, 0, {
    name: collaboratorSearch,
    role: "COLABORADOR",
    active: "",
  });

  const managerOptions = createUserOptions(
    managersData?.content ?? [],
    section?.managers ?? [],
  );
  const collaboratorOptions = createUserOptions(
    collaboratorsData?.content ?? [],
    section?.collaborators ?? [],
  );

  const { mutateAsync: createSection, isPending: isCreating } =
    useCreateSection(setErrors, setIsOpen);

  async function handleSubmit() {
    const isValid = validateSectionForm(form, setErrors, !!section);
    if (!isValid) {
      return;
    }
    await createSection(form);
  }

  function updateUsers(
    field: "managersUsernames" | "collaboratorsUsernames",
    value: string[],
  ) {
    setErrors(INITIAL_SECTION_ERRORS);
    setForm((previous) => ({ ...previous, [field]: value }));
  }

  return (
    <FormDialog
      isOpen={isOpen}
      setIsOpen={setIsOpen}
      title={section ? "Editar sección" : "Crear sección"}
      handleSubmit={handleSubmit}
      disabled={!token || isCreating}
      submitButtonText={section ? "Guardar cambios" : "Crear sección"}
    >
      <CustomInput
        label="Nombre"
        name="name"
        placeholder="Nombre de la sección"
        value={form.name}
        error={errors.name}
        onChange={(event) => handleChange(event, form, setErrors, setForm)}
        required
        maxLength={MAX_LENGTH.NAME}
      />

      <Separator />
      <Heading as="h2" size="md">
        Encargados
      </Heading>

      <CustomSearchBar
        placeholder="Buscar encargados..."
        value={managerSearch}
        onChange={(event) => setManagerSearch(event.currentTarget.value)}
        disabled={isCreating}
      />
      {isManagersError ? (
        <TextSecondary>No se pudieron cargar los encargados.</TextSecondary>
      ) : (
        <UserSelectLoading loading={isLoadingManagers}>
          <CustomSelect
            label="Encargados"
            placeholder="Selecciona los encargados"
            options={managerOptions}
            multiple
            required
            value={form.managersUsernames}
            onValueChange={({ value }) =>
              updateUsers("managersUsernames", value)
            }
            error={errors.managersUsernames}
          />
        </UserSelectLoading>
      )}

      <Separator />
      <Heading as="h2" size="md">
        Colaboradores
      </Heading>

      <CustomSearchBar
        placeholder="Buscar colaboradores..."
        value={collaboratorSearch}
        onChange={(event) => setCollaboratorSearch(event.currentTarget.value)}
        disabled={isCreating}
      />
      {isCollaboratorsError ? (
        <TextSecondary>No se pudieron cargar los colaboradores.</TextSecondary>
      ) : (
        <UserSelectLoading loading={isLoadingCollaborators}>
          <CustomSelect
            label="Colaboradores"
            placeholder="Selecciona los colaboradores"
            options={collaboratorOptions}
            multiple
            value={form.collaboratorsUsernames}
            onValueChange={({ value }) =>
              updateUsers("collaboratorsUsernames", value)
            }
            error={errors.collaboratorsUsernames}
          />
        </UserSelectLoading>
      )}
    </FormDialog>
  );
}

function createUserOptions(users: User[], selectedUsers: User[]) {
  const usersByUsername = new Map(
    [...selectedUsers, ...users].map((user) => [user.username, user]),
  );

  return [...usersByUsername.values()].map((user) => ({
    value: user.username,
    label: `${user.name} ${user.surname} (@${user.username})`,
  }));
}

function UserSelectLoading({
  loading,
  children,
}: {
  loading: boolean;
  children: React.ReactNode;
}) {
  return loading ? (
    <HStack justify="center">
      <Spinner size="sm" color="principal.800" />
    </HStack>
  ) : (
    children
  );
}
