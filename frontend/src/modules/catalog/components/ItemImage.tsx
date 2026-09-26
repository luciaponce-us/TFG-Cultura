import { useState } from "react";
import { Box, Image } from "@chakra-ui/react";

import type { Item, ItemType } from "../types";
import { getPlaceholder } from "../utils/item.utils";

export function ItemImage<T extends Item>({
  item,
  type,
}: {
  item: T;
  type: ItemType;
}) {
  const [hasImageError, setHasImageError] = useState(false);
  return (
    <Box
      minH={0}
      aspectRatio="2/3"
      borderRadius="md"
      w="100%"
      alignItems="center"
      justifyContent="center"
      display="flex"
      overflow="hidden"
      textAlign="center"
      color="gray.500"
    >
      {hasImageError ? (
        <Image
          src={getPlaceholder(type)}
          alt={item.name}
          w="100%"
          h="100%"
          minH={0}
          objectFit="contain"
          borderRadius="md"
          aspectRatio="2/3"
        />
      ) : (
        <Image
          src={item.imageUrl ?? getPlaceholder(type)}
          alt={item.name}
          w="100%"
          h="100%"
          minH={0}
          objectFit="contain"
          borderRadius="md"
          aspectRatio="2/3"
          onError={() => setHasImageError(true)}
        />
      )}
    </Box>
  );
}
