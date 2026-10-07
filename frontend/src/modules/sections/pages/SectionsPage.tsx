import { Flex, Heading, Table, VStack } from "@chakra-ui/react";
import {
  ConfirmDialog,
  CustomButton,
  TextSecondary,
} from "@/modules/core/components";
import { useState } from "react";
import { IconPencil, IconPlus, IconTrash } from "@tabler/icons-react";
import { useDeleteSection, useSections } from "../hooks";
import type { Section } from "../types";
import {
  AddCollaboratorDialog,
  AddManagerDialog,
  SectionFormDialog,
  UserCard,
} from "../components";
import { useAuth } from "@/modules/core/context/useAuth";

export function SectionsPage() {
  const { isSuperAdmin } = useAuth();
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const { data: sections, isLoading, isError } = useSections();
  const headers = isSuperAdmin
    ? ["Nombre", "Encargados", "Colaboradores", "Acciones"]
    : ["Nombre", "Encargados", "Colaboradores"];

  let content;

  if (isLoading) {
    content = <TextSecondary>Cargando...</TextSecondary>;
  } else if (isError) {
    content = (
      <TextSecondary>No se pudieron cargar las secciones.</TextSecondary>
    );
  } else if (sections && sections.length > 0) {
    content =
      sections && sections.length > 0 ? (
        <Table.ScrollArea
          borderWidth="1px"
          rounded="md"
          w="100%"
          overflowX="auto"
        >
          <Table.Root size="sm" stickyHeader showColumnBorder>
            <Table.Header>
              <Table.Row bg="principal.200">
                {headers.map((header) => (
                  <Table.ColumnHeader
                    fontWeight="bold"
                    textAlign="center"
                    key={header}
                  >
                    {header}
                  </Table.ColumnHeader>
                ))}
              </Table.Row>
            </Table.Header>
            <Table.Body>
              {sections.map((section) => (
                <SectionRow section={section} isSuperAdmin={isSuperAdmin} />
              ))}
            </Table.Body>
          </Table.Root>
        </Table.ScrollArea>
      ) : (
        <TextSecondary>No hay secciones disponibles.</TextSecondary>
      );
  }

  return (
    <>
      <Flex
        bg="background"
        borderRadius="xl"
        boxShadow="lg"
        p={6}
        direction="column"
        align="center"
        justify="flex-start"
        gap={6}
      >
        <Heading as="h1">Administración de secciones</Heading>
        {isSuperAdmin && (
          <CustomButton onClick={() => setIsCreateDialogOpen(true)}>
            <IconPlus />
            Crear sección
          </CustomButton>
        )}
        {content}
      </Flex>
      {isCreateDialogOpen && isSuperAdmin && (
        <SectionFormDialog isOpen setIsOpen={setIsCreateDialogOpen} />
      )}
    </>
  );
}

function SectionRow({
  section,
  isSuperAdmin,
}: {
  section: Section;
  isSuperAdmin: boolean;
}) {
  const [isAddManagerDialogOpen, setIsAddManagerDialogOpen] = useState(false);
  const [isAddCollaboratorDialogOpen, setIsAddCollaboratorDialogOpen] =
    useState(false);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);

  const {
    mutateAsync: deleteSection,
    isPending: isDeleting,
    isError: isDeleteError,
  } = useDeleteSection();

  async function handleDeleteSection() {
    if (!isSuperAdmin) return;
    await deleteSection({ sectionId: section.id });
    if (!isDeleting && !isDeleteError) {
      setIsDeleteDialogOpen(false);
    }
  }

  return (
    <>
      <Table.Row
        key={section.id}
        pointerEvents={isDeleting ? "none" : "auto"}
        opacity={isDeleting ? 0.5 : 1}
      >
        <Table.Cell
          key={`${section.id}-name`}
          textAlign="center"
          alignItems="center"
        >
          {section.name}
        </Table.Cell>
        <Table.Cell
          key={`${section.id}-managers`}
          textAlign="center"
          alignItems="center"
        >
          <VStack gap={2}>
            {section.managers.length > 0 ? (
              section.managers.map((manager) => (
                <UserCard
                  key={manager.username}
                  user={manager}
                  sectionId={section.id}
                  isSuperAdmin={isSuperAdmin}
                />
              ))
            ) : (
              <TextSecondary>No hay encargados</TextSecondary>
            )}
            {isSuperAdmin && (
              <CustomButton onClick={() => setIsAddManagerDialogOpen(true)}>
                <IconPlus />
                Añadir encargado
              </CustomButton>
            )}
          </VStack>
        </Table.Cell>
        <Table.Cell
          key={`${section.id}-collaborators`}
          textAlign="center"
          alignItems="center"
        >
          <VStack gap={2}>
            {section.collaborators.length > 0 ? (
              section.collaborators.map((collaborator) => (
                <UserCard
                  key={collaborator.username}
                  user={collaborator}
                  sectionId={section.id}
                  isSuperAdmin={isSuperAdmin}
                />
              ))
            ) : (
              <TextSecondary>No hay colaboradores</TextSecondary>
            )}
            {isSuperAdmin && (
              <CustomButton
                onClick={() => setIsAddCollaboratorDialogOpen(true)}
              >
                <IconPlus />
                Añadir colaborador
              </CustomButton>
            )}
          </VStack>
        </Table.Cell>
        {isSuperAdmin && (
          <Table.Cell
            key={`${section.id}-actions`}
            textAlign="center"
            alignItems="center"
          >
            <VStack gap={2}>
              <CustomButton
                onClick={() => {
                  setIsEditDialogOpen(true);
                }}
                disabled={isDeleting}
              >
                <IconPencil />
              </CustomButton>
              <CustomButton
                color="rojo"
                onClick={() => setIsDeleteDialogOpen(true)}
                loading={isDeleting}
              >
                <IconTrash />
              </CustomButton>
            </VStack>
          </Table.Cell>
        )}
      </Table.Row>

      {isDeleteDialogOpen && isSuperAdmin && (
        <ConfirmDialog
          isOpen
          setIsOpen={setIsDeleteDialogOpen}
          handleAction={() => void handleDeleteSection()}
          title="Eliminar sección"
          message={`¿Estás seguro de que deseas eliminar la sección "${section.name}"? Esta acción no se puede deshacer.`}
        />
      )}

      {isAddManagerDialogOpen && isSuperAdmin && (
        <AddManagerDialog
          isOpen
          setIsOpen={setIsAddManagerDialogOpen}
          sectionId={section.id}
          managers={section.managers.map((manager) => manager.username)}
        />
      )}

      {isAddCollaboratorDialogOpen && isSuperAdmin && (
        <AddCollaboratorDialog
          isOpen
          setIsOpen={setIsAddCollaboratorDialogOpen}
          sectionId={section.id}
          collaborators={section.collaborators.map(
            (collaborator) => collaborator.username,
          )}
        />
      )}
      {isEditDialogOpen && isSuperAdmin && (
        <SectionFormDialog
          isOpen
          setIsOpen={setIsEditDialogOpen}
          section={section}
        />
      )}
    </>
  );
}
