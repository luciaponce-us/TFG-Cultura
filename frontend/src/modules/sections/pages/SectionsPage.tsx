import { Flex, Heading, Table, VStack } from "@chakra-ui/react";
import {
  ConfirmDialog,
  CustomButton,
  TextSecondary,
} from "@/modules/core/components";
import { useState } from "react";
import { IconPencil, IconPlus, IconTrash } from "@tabler/icons-react";
import { useSections } from "../hooks";
import type { Section } from "../types";
import { AddManagerDialog, UserCard } from "../components";

export function SectionsPage() {
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const { data: sections, isLoading, isError } = useSections();
  const headers = ["Nombre", "Encargados", "Colaboradores", "Acciones"];

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
        <Table.ScrollArea borderWidth="1px" rounded="md" w="100%" overflowX="auto">
              <Table.Root size="sm" stickyHeader showColumnBorder>
                <Table.Header>
                  <Table.Row bg="principal.200">
      {headers.map((header) => (
        <Table.ColumnHeader fontWeight="bold" textAlign="center" key={header}>
          {header}
        </Table.ColumnHeader>
      ))}
    </Table.Row>
                </Table.Header>
        <Table.Body>
          {sections.map((section) => (
            <SectionRow section={section} />
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
        <CustomButton onClick={() => setIsCreateDialogOpen(true)}>
          <IconPlus />
          Crear sección
        </CustomButton>
        {content}
      </Flex>
      {isCreateDialogOpen && (
        <TextSecondary>Crear sección dialog is open</TextSecondary>
      )}
    </>
  );
}

function SectionRow({ section }: { section: Section }) {
  const [isAddManagerDialogOpen, setIsAddManagerDialogOpen] = useState(false);

  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  return (
    <>
      <Table.Row
        key={section.id}
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
          {section.managers.length>0? section.managers.map((manager) => <UserCard key={manager.username} user={manager} sectionId={section.id} />):<TextSecondary>No hay encargados</TextSecondary>}
          <CustomButton onClick={() => setIsAddManagerDialogOpen(true)}>
            <IconPlus />Añadir encargado
          </CustomButton>
          </VStack>
        </Table.Cell>
        <Table.Cell
          key={`${section.id}-collaborators`}
          textAlign="center"
          alignItems="center"
        >
          {section.collaborators.length>0? section.collaborators.map((collaborator) => <UserCard key={collaborator.username} user={collaborator} sectionId={section.id} />):<TextSecondary>No hay colaboradores</TextSecondary>}
        </Table.Cell>
        <Table.Cell
          key={`${section.id}-actions`}
          textAlign="center"
          alignItems="center"
        >
          <VStack gap={2}>
            <CustomButton onClick={() => {}}>
              <IconPencil />
            </CustomButton>
            <CustomButton
              color="rojo"
              onClick={() => setIsDeleteDialogOpen(true)}
            >
              <IconTrash />
            </CustomButton>
          </VStack>
        </Table.Cell>
      </Table.Row>

      {isDeleteDialogOpen && (
        <ConfirmDialog
          isOpen
          setIsOpen={setIsDeleteDialogOpen}
          handleAction={() => console.log("Delete section")}
          title="Eliminar sección"
          message={`¿Estás seguro de que deseas eliminar la sección "${section.name}"? Esta acción no se puede deshacer.`}
        />
      )}

      {isAddManagerDialogOpen && (
        <AddManagerDialog
          isOpen
          setIsOpen={setIsAddManagerDialogOpen}
          sectionId={section.id}
          managers={section.managers.map((manager) => manager.username)}
        />
      )}
    </>
  );
}
