import { useAuth } from "@/modules/core/context/useAuth";
import { useState, type Dispatch, type SetStateAction } from "react";
import {
  FILTERS_GET_ALL_ITEMS_DEFAULT,
  type FiltersGetAllItems,
  type Item,
  type ItemType,
} from "../types";
import { TextSecondary } from "@/modules/core/components/text/TextSecondary";
import { toaster } from "@/modules/core/components/toaster/toaster";
import { Box, Grid, Heading, Link, VStack } from "@chakra-ui/react";
import {
  CustomButton,
  CustomPagination,
  CustomSearchBar,
  SideBar,
} from "@/modules/core/components";
import { IconPlus } from "@tabler/icons-react";
import type { Paginated } from "@/modules/core/types";
import type { UseQueryResult } from "@tanstack/react-query";
import { ItemCard } from "../components";
import type { CreateItemDialogProps } from "../types/props";
import { CategoriesSelect } from "@/modules/categories/components";
import { useDebounce } from "@/modules/core/hooks/useDebounce";

interface ItemsPageProps<T extends { id: string; name?: string }> {
  getAllHook: (
    page: number,
    filters: FiltersGetAllItems,
  ) => UseQueryResult<Paginated<T>>;
  initialFilters?: FiltersGetAllItems;
  title: string;
  loadText: string;
  errorText: { title: string; description: string };
  emptyText: (filters: boolean) => string;
  type: ItemType;
  createText?: string;
  sectionDefaultValue?: string;
  CreateDialogComponent?: React.ComponentType<CreateItemDialogProps>;
}

export function ItemsPage<T extends Item>({
  getAllHook,
  initialFilters = FILTERS_GET_ALL_ITEMS_DEFAULT,
  title,
  loadText,
  errorText,
  emptyText,
  type,
  createText,
  CreateDialogComponent,
  sectionDefaultValue,
}: ItemsPageProps<T>) {
  const { isAdmin } = useAuth();
  const [isCreateDialogOpen, setIsCreateDialogOpen] = useState(false);
  const [filters, setFilters] = useState<FiltersGetAllItems>(initialFilters);
  const [search, setSearch] = useState("");
  const debouncedSearch = useDebounce(search, 400);
  const filtersWithSearch = {
    ...filters,
    nameContains: debouncedSearch,
  };
  const [page, setPage] = useState<number>(0);
  const {
    data: paginatedItems,
    isLoading,
    error,
    isError,
  } = getAllHook(page, filtersWithSearch);

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
      return (
        <VStack flex={1} justify="center" align="center">
          <TextSecondary fontSize="sm">
            {emptyText(!!Object.keys(filters).length)}
          </TextSecondary>
          {Object.keys(filters).length > 0 && (
            <Link
              fontSize="sm"
              onClick={() => {
                setPage(0);
                setFilters(initialFilters);
              }}
            >
              Eliminar filtros
            </Link>
          )}
        </VStack>
      );
    }

    return (
      <Grid
        templateColumns={{ base: "1fr 1fr", md: "1fr 1fr 1fr 1fr" }}
        gap={2}
        w="100%"
      >
        {content?.map((item) => (
          <ItemCard
            key={item.id}
            item={item}
            CreateItemDialog={CreateDialogComponent!}
            type={type}
          />
        ))}
      </Grid>
    );
  }

  return (
    <Grid
      templateColumns={{ base: "1fr", md: "1fr 2fr 1fr" }}
      gap={10}
      flex={1}
      maxW="80vw"
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
          <FiltersSection
            filters={filters}
            setFilters={setFilters}
            setPage={setPage}
            setSearch={setSearch}
          />
        </VStack>
      </SideBar>
      <VStack
        bg="background"
        borderRadius="xl"
        boxShadow="lg"
        p={{ base: 4, md: 6 }}
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
        <Box w="100%" display={{ base: "block", md: "none" }}>
          <FiltersSection
            filters={filters}
            setFilters={setFilters}
            setPage={setPage}
            setSearch={setSearch}
          />
        </Box>
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
      {CreateDialogComponent && isCreateDialogOpen && (
        <CreateDialogComponent
          isOpen
          setIsOpen={setIsCreateDialogOpen}
          sectionDefaultValue={sectionDefaultValue}
        />
      )}
    </Grid>
  );
}

function FiltersSection({
  filters,
  setFilters,
  setPage,
  setSearch,
}: {
  filters: FiltersGetAllItems;
  setFilters: Dispatch<SetStateAction<FiltersGetAllItems>>;
  setPage: Dispatch<SetStateAction<number>>;
  setSearch: Dispatch<SetStateAction<string>>;
}) {
  return (
    <VStack w="100%" gap={3} align="stretch">
      <CustomSearchBar
        placeholder="Buscar..."
        onChange={(e) => {
          setPage(0);
          setSearch(e.currentTarget.value);
        }}
      />
      <CategoriesSelect
        form={filters}
        setForm={(newFilters) => {
          setPage(0);
          setFilters(newFilters);
        }}
      />
    </VStack>
  );
}
