import { useAuth } from "@/modules/core/context/useAuth";
import { useState } from "react";
import {
  FILTERS_GET_ALL_ITEMS_DEFAULT,
  type FiltersGetAllItems as Filters,
} from "../types";
import { TextSecondary } from "@/modules/core/components/text/TextSecondary";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { Grid, Heading, Link, VStack } from "@chakra-ui/react";
import {
  CustomButton,
  CustomPagination,
  CustomSearchBar,
  SideBar,
} from "@/modules/core/components";
import { IconPlus } from "@tabler/icons-react";
import type { Paginated } from "@/modules/core/types";
import type { UseQueryResult } from "@tanstack/react-query";

interface ItemsPageProps<
  T extends { id: string; name?: string },
  TFilters extends Filters = Filters,
> {
  getAllHook: (page: number, filters: TFilters) => UseQueryResult<Paginated<T>>;
  initialFilters?: TFilters;
  renderItem?: (item: T) => React.ReactNode;
  title: string;
  loadText: string;
  errorText: { title: string; description: string };
  emptyText: string;
  createText?: string;
  CreateDialogComponent?: React.ComponentType<{
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
    token?: string | null;
  }>;
}

export function ItemsPage<
  T extends { id: string; name?: string },
  TFilters extends Filters = Filters,
>({
  getAllHook,
  initialFilters = FILTERS_GET_ALL_ITEMS_DEFAULT as TFilters,
  renderItem = (item) => <TextSecondary>{item.name}</TextSecondary>,
  title,
  loadText,
  errorText,
  emptyText,
  createText,
  CreateDialogComponent,
}: ItemsPageProps<T, TFilters>) {
  const { token, isAdmin } = useAuth();
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [filters, setFilters] = useState<TFilters>(initialFilters);
  const [page, setPage] = useState<number>(0);
  const {
    data: paginatedItems,
    isLoading,
    error,
    isError,
  } = getAllHook(page, filters);

  const content = paginatedItems?.content;

  function renderItems() {
    if (isLoading) {
      return <TextSecondary>{loadText}</TextSecondary>;
    }
    if (isError) {
      console.error(error);
      toaster.create({
        title: errorText.title,
        description: errorText.description,
        type: "error",
      });
      return <TextSecondary>{errorText.title}</TextSecondary>;
    }

    if (!paginatedItems || content?.length === 0) {
      return <TextSecondary>{emptyText}</TextSecondary>;
    }

    return (
      <VStack align="stretch" gap={4} w="100%">
        {content?.map((item) => (
          <div key={item.id}>{renderItem(item)}</div>
        ))}
      </VStack>
    );
  }

  return (
    <Grid
      templateColumns={{ base: "1fr", md: "1fr 2fr" }}
      gap={10}
      flex={1}
      maxW="100vw"
    >
      <SideBar>
        <VStack align="start" gap={4} w="100%" minW="210px">
          <Heading as="h1">Filtros</Heading>
          <Link
            variant="underline"
            color="principal.500"
            onClick={() => {
              setPage(0);
              setFilters(initialFilters);
            }}
          >
            Eliminar filtros
          </Link>
          <CustomSearchBar
            placeholder="Buscar..."
            onChange={(e) => {
              setPage(0);
              setFilters({
                ...initialFilters,
                nameContains: e.currentTarget.value,
              });
            }}
          />
        </VStack>
      </SideBar>
      <VStack
        bg="background"
        borderRadius="xl"
        boxShadow="lg"
        p={6}
        align="center"
        justify="flex-start"
        w="100%"
        minW={{ base: "100%", md: "800px" }}
        maxW="800px"
        h="fit-content"
        minH="80vh"
        gap={6}
        flex={1}
      >
        <Heading as="h1">{title}</Heading>

        {isAdmin && createText && CreateDialogComponent && (
          <CustomButton
            onClick={() => {
              setIsCreateDialogOpen(true);
            }}
          >
            <IconPlus />
            {createText}
          </CustomButton>
        )}
        {renderItems()}
        {content && paginatedItems.totalPages > 1 && (
          <CustomPagination
            setPage={setPage}
            page={page}
            totalElements={paginatedItems.totalElements}
            size={paginatedItems.size}
          />
        )}
      </VStack>
      {CreateDialogComponent && (
        <CreateDialogComponent
          isOpen={isCreateDialogOpen}
          setIsOpen={setIsCreateDialogOpen}
          token={token}
        />
      )}
    </Grid>
  );
}
