import { useAuth } from "@/modules/core/context/useAuth";
import { useState } from "react";
import {
  INITIAL_SECTION_ERRORS,
  INITIAL_SECTION_FORM,
  type Section,
  type SectionErrors,
  type SectionRequest,
} from "../../types";
import {
  CustomInput,
  CustomSearchBar,
  CustomSelect,
  FormDialog,
  TextSecondary,
} from "@/modules/core/components";
import { handleChange } from "@/modules/core/utils/utils";
import { MAX_LENGTH, validateSectionForm } from "../../validations";
import { useCreateSection, useUpdateSection } from "../../hooks";
import { useUsers } from "@/modules/users/hooks";
import { Heading, HStack, Separator, Spinner } from "@chakra-ui/react";
import { createUserOptions, toRequest } from "../../utils";

interface SectionFormDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: (isOpen: boolean) => void;
  readonly section?: Section;
}

export function SectionFormDialog({
  isOpen,
  setIsOpen,
  section,
}: SectionFormDialogProps) {
  const { token } = useAuth();
  const [errors, setErrors] = useState<SectionErrors>(INITIAL_SECTION_ERRORS);
  const [form, setForm] = useState<SectionRequest>(toRequest(section));

  function resetForm() {
    setForm(INITIAL_SECTION_FORM);
    setErrors(INITIAL_SECTION_ERRORS);
  }

  const { mutateAsync: createSection, isPending: isCreating } =
    useCreateSection(setErrors, setIsOpen);
  const { mutateAsync: updateSection, isPending: isUpdating } =
    useUpdateSection(section?.id, form, setErrors, setIsOpen, resetForm);

  async function handleSubmit() {
    const isValid = validateSectionForm(form, setErrors, !!section);
    if (!isValid) {
      return;
    }

    if (section) {
      await updateSection();
    } else {
      await createSection(form);
    }
  }

  /**
   * Update the list of managers or collaborators in the form state.
   * @param field - The field to update ("managersUsernames" or "collaboratorsUsernames").
   * @param value - The new list of usernames to set for the specified field.
   */
  function updateUsers(
    field: "managersUsernames" | "collaboratorsUsernames",
    value: string[],
  ) {
    setErrors(INITIAL_SECTION_ERRORS);
    setForm((previous) => ({ ...previous, [field]: value }));
  }

  if (!token) {
    return;
  }

  return (
    <FormDialog
      isOpen={isOpen}
      setIsOpen={setIsOpen}
      title={section ? `Editar sección "${section.name}"` : "Crear sección"}
      handleSubmit={handleSubmit}
      disabled={!token || isCreating || isUpdating}
      submitButtonText={section ? "Guardar cambios" : "Crear sección"}
      resetForm={resetForm}
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

      <ManagersSelect
        token={token}
        section={section}
        form={form}
        errors={errors}
        updateUsers={updateUsers}
        isSubmitting={isCreating || isUpdating}
      />

      <CollaboratorsSelect
        token={token}
        section={section}
        form={form}
        errors={errors}
        updateUsers={updateUsers}
        isSubmitting={isCreating || isUpdating}
      />
    </FormDialog>
  );
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

function ManagersSelect({
  token,
  section,
  form,
  errors,
  updateUsers,
  isSubmitting,
}: {
  token: string;
  section?: Section;
  form: SectionRequest;
  errors: SectionErrors;
  updateUsers: (
    field: "managersUsernames" | "collaboratorsUsernames",
    value: string[],
  ) => void;
  isSubmitting: boolean;
}) {
  const [managerSearch, setManagerSearch] = useState("");

  const {
    data: managersData,
    isLoading: isLoadingManagers,
    isError: isManagersError,
  } = useUsers(token, 0, {
    name: managerSearch,
    role: "ENCARGADO",
    active: "",
  });

  const managerOptions = createUserOptions(managersData, section?.managers);

  return (
    <>
      <Separator />
      <Heading as="h2" size="md">
        Encargados
      </Heading>

      <CustomSearchBar
        placeholder="Buscar encargados..."
        value={managerSearch}
        onChange={(event) => setManagerSearch(event.currentTarget.value)}
        disabled={isSubmitting}
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
    </>
  );
}

function CollaboratorsSelect({
  token,
  section,
  form,
  errors,
  updateUsers,
  isSubmitting,
}: {
  token: string;
  section?: Section;
  form: SectionRequest;
  errors: SectionErrors;
  updateUsers: (
    field: "managersUsernames" | "collaboratorsUsernames",
    value: string[],
  ) => void;
  isSubmitting: boolean;
}) {
  const [collaboratorSearch, setCollaboratorSearch] = useState("");

  const {
    data: collaboratorsData,
    isLoading: isLoadingCollaborators,
    isError: isCollaboratorsError,
  } = useUsers(token, 0, {
    name: collaboratorSearch,
    role: "COLABORADOR",
    active: "",
  });

  const collaboratorOptions = createUserOptions(
    collaboratorsData,
    section?.collaborators,
  );

  return (
    <>
      <Separator />
      <Heading as="h2" size="md">
        Colaboradores
      </Heading>

      <CustomSearchBar
        placeholder="Buscar colaboradores..."
        value={collaboratorSearch}
        onChange={(event) => setCollaboratorSearch(event.currentTarget.value)}
        disabled={isSubmitting}
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
    </>
  );
}
