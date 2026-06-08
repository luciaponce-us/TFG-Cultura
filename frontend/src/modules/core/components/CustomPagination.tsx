import { ButtonGroup, IconButton, Pagination } from "@chakra-ui/react";
import type { Paginated } from "../types";
import { IconArrowNarrowLeft, IconArrowNarrowRight } from "@tabler/icons-react";

interface CustomPaginationProps extends Partial<Paginated<unknown>> {
  setPage: (page: number) => void;
  page: number;
}

export const CustomPagination = ({
  setPage,
  page,
  totalElements,
  size,
}: CustomPaginationProps) => {
  // Conversión: Spring Boot usa 0-based indexing, Chakra UI usa 1-based
  const chakraPage = (page ?? 0) + 1;

  return (
    <Pagination.Root
      count={totalElements}
      pageSize={size}
      page={chakraPage}
      onPageChange={(pageInfo) => setPage(pageInfo.page - 1)}
    >
      <ButtonGroup variant="ghost" size="sm">
        <Pagination.PrevTrigger asChild>
          <IconButton>
            <IconArrowNarrowLeft />
          </IconButton>
        </Pagination.PrevTrigger>

        <Pagination.Items
          render={(pageItem) => (
            <IconButton variant={{ base: "ghost", _selected: "outline" }}>
              {pageItem.value}
            </IconButton>
          )}
        />

        <Pagination.NextTrigger asChild>
          <IconButton>
            <IconArrowNarrowRight />
          </IconButton>
        </Pagination.NextTrigger>
      </ButtonGroup>
    </Pagination.Root>
  );
};
