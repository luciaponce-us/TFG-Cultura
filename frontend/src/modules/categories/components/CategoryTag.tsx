import { Flex, Text } from "@chakra-ui/react";
import type { Category } from "../types";
import { isLightColor } from "../utils";

export function CategoryTag({ category }: { category: Category }) {
  const isLight = isLightColor(category.color);

  return (
    <Flex
      bg={category.color}
      border={isLight ? "1px solid #1E1E1E" : "none"}
      borderRadius="md"
      px={2}
      py={1}
      align="center"
      justify="center"
    >
      <Text color={isLight ? "#1E1E1E" : "white"} fontWeight="bold">
        {category.name}
      </Text>
    </Flex>
  );
}