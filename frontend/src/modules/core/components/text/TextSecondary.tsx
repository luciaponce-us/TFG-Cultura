import { Text } from "@chakra-ui/react";
import type { TextProps } from "@chakra-ui/react";

export const TextSecondary = (props: TextProps) => {
  return (
    <Text fontFamily="body" fontSize="xs" color="text.secondary" {...props} />
  );
};
