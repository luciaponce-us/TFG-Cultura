import { AspectRatio, VStack } from "@chakra-ui/react";
import { useState } from "react";
import type { Item } from "../types";

export function ItemTrailer({
  item,
  trailerUrl,
}: {
  item: Item;
  trailerUrl: string;
}) {
  const [isIframeError, setIsIframeError] = useState(false);

  return (
    <VStack align="start" gap={2} w={{ base: "100%", md: "320px" }}>
      <AspectRatio ratio={16 / 9} w="100%" borderRadius="md" overflow="hidden">
        {isIframeError ? (
          <VStack
            w="100%"
            h="100%"
            justify="center"
            align="center"
            bg="gray.200"
          >
            <p>Error al cargar el trailer</p>
          </VStack>
        ) : (
          <iframe
            src={trailerUrl}
            title={`Trailer de ${item.name}`}
            allowFullScreen
            onError={(e) => {
              console.error("Error al cargar trailer:", e);
              setIsIframeError(true);
            }}
          />
        )}
      </AspectRatio>
    </VStack>
  );
}
