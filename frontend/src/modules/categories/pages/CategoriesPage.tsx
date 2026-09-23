import { Flex, Heading, HStack } from "@chakra-ui/react";
import {
  ConfirmDialog,
  CustomButton,
  TextSecondary,
} from "@/modules/core/components";
import { useState } from "react";
import { useCategories, useDeleteCategory } from "../hooks";
import { CreateCategoryDialog } from "../components";
import type { Category } from "../types";
import { CategoryTag } from "../components/CategoryTag";
import { IconPencil, IconPlus, IconTrash } from "@tabler/icons-react";

export function CategoriesPage() {
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const { data: categories, isLoading, isError } = useCategories();

  let content;

  if (isLoading) {
    content = <TextSecondary>Cargando...</TextSecondary>;
  } else if (isError) {
    content = (
      <TextSecondary>No se pudieron cargar las categorías.</TextSecondary>
    );
  } else if (categories && categories.length > 0) {
    content =
      categories && categories.length > 0 ? (
        <Flex direction="column" gap={4} width="100%">
          {categories.map((category) => (
            <CategoryCard category={category} />
          ))}
        </Flex>
      ) : (
        <TextSecondary>No hay juegos de categorías disponibles.</TextSecondary>
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
        <Heading as="h1">Categorías</Heading>
        <CustomButton onClick={() => setIsCreateDialogOpen(true)}>
          <IconPlus />
          Crear categoría
        </CustomButton>
        {content}
      </Flex>
      {isCreateDialogOpen && (
        <CreateCategoryDialog
          isOpen={isCreateDialogOpen}
          setIsOpen={setIsCreateDialogOpen}
        />
      )}
    </>
  );
}

function CategoryCard({ category }: { category: Category }) {
  const [isEditOpen, setIsEditOpen] = useState(false);
  const { mutateAsync: deleteCategory, isPending: isDeleting } =
    useDeleteCategory(category.id);
  const [isDeleteDialogOpen, setIsDeleteDialogOpen] = useState(false);
  return (
    <>
      <HStack justify="space-between" align="center">
        <CategoryTag category={category} />
        <HStack>
          <CustomButton onClick={() => setIsEditOpen(true)}>
            <IconPencil />
          </CustomButton>
          <CustomButton
            color="rojo"
            onClick={() => setIsDeleteDialogOpen(true)}
            loading={isDeleting}
          >
            <IconTrash />
          </CustomButton>
        </HStack>
      </HStack>
      {isEditOpen && (
        <CreateCategoryDialog
          isOpen
          setIsOpen={setIsEditOpen}
          categoryId={category.id}
        />
      )}
      {isDeleteDialogOpen && (
        <ConfirmDialog
          isOpen
          setIsOpen={setIsDeleteDialogOpen}
          handleAction={() => void deleteCategory()}
          title="Eliminar categoría"
          message={`¿Estás seguro de que deseas eliminar la categoría "${category.name}"? Esta acción no se puede deshacer.`}
        />
      )}
    </>
  );
}
