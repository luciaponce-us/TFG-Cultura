import { HStack, Text } from "@chakra-ui/react";
import type { Item } from "../types";
import { useAuth } from "@/modules/core/context/useAuth";
import { CustomButton } from "@/modules/core/components";
import { IconPencil } from "@tabler/icons-react";
import { useState } from "react";

export function ItemCard<T extends Item>({
  item,
  CreateItemDialog,
}: {
  item: T;
  CreateItemDialog: React.ComponentType<{
    isOpen: boolean;
    setIsOpen: (isOpen: boolean) => void;
    itemId?: string;
  }>;
}) {
  const { isAdmin } = useAuth();
  const [isEditOpen, setIsEditOpen] = useState(false);
  return (
    <>
      <HStack>
        <Text>{`${item.name}`}</Text>
        {isAdmin && (
          <CustomButton onClick={() => setIsEditOpen(true)}>
            <IconPencil />
          </CustomButton>
        )}
      </HStack>
      {isEditOpen && (
        <CreateItemDialog isOpen setIsOpen={setIsEditOpen} itemId={item.id} />
      )}
    </>
  );
}
