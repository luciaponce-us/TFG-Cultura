import { Table } from "@chakra-ui/react";

import { useIsMobile } from "@/modules/core/utils/utils";
import {
  defaultHeaders,
  defaultRowsContent,
  mobileHeaders,
  mobileRowsContent,
} from "../config/usersTable.config";

import type { User } from "../types";

export function UsersTable({ users }: { readonly users: User[] }) {
  return (
    <Table.ScrollArea borderWidth="1px" rounded="md" w="100%" overflowX="auto">
      <Table.Root size="sm" stickyHeader showColumnBorder>
        <Table.Header>
          <UsersTableHeader />
        </Table.Header>

        <Table.Body>
          {users.map((user) => (
            <UserRow key={user.username} user={user} />
          ))}
        </Table.Body>
      </Table.Root>
    </Table.ScrollArea>
  );
}

function UsersTableHeader() {
  const isMobile = useIsMobile();
  const headers = isMobile ? mobileHeaders : defaultHeaders;

  return (
    <Table.Row bg="principal.200">
      {headers.map((header) => (
        <Table.ColumnHeader fontWeight="bold" textAlign="center" key={header}>
          {header}
        </Table.ColumnHeader>
      ))}
    </Table.Row>
  );
}

function UserRow({ user }: { readonly user: User }) {
  const isMobile = useIsMobile();
  const rowsContent = isMobile
    ? mobileRowsContent(user)
    : defaultRowsContent(user);

  return (
    <Table.Row
      key={user.username}
      _hover={{ bg: "principal.50" }}
      cursor="pointer"
    >
      {rowsContent.map((item) => (
        <Table.Cell
          key={`${user.username}-${item.key}`}
          textAlign="center"
          alignItems="center"
        >
          {item.content}
        </Table.Cell>
      ))}
    </Table.Row>
  );
}
