import { useState } from "react";
import { Box, Image } from "@chakra-ui/react";

import type { Item, ItemType } from "../types";
import { getPlaceholder } from "../utils/item.utils";
import type { RolSaga } from "../types/rolgame";
import { PLACEHOLDER } from "@/modules/core/utils/utils";

export function ItemImage<T extends Item | RolSaga>({
  item,
  type,
}: {
  item: T;
  type?: ItemType;
}) {
  const [hasImageError, setHasImageError] = useState(false);
  const placeholderUrl = type ? getPlaceholder(type) : PLACEHOLDER.ROLSAGA;
  const imageUrl = item.imageUrl ?? placeholderUrl;
  
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
          src={placeholderUrl}
          alt={item.name}
          w="100%"
          h="100%"
          minH={0}
          objectFit="cover"
          borderRadius="md"
          aspectRatio="2/3"
        />
      ) : (
        <Image
          src={imageUrl}
          alt={item.name}
          w="100%"
          h="100%"
          minH={0}
          objectFit="cover"
          borderRadius="md"
          aspectRatio="2/3"
          onError={() => setHasImageError(true)}
        />
      )}
    </Box>
  );
}
