import { Box, Flex, Image, Heading } from "@chakra-ui/react";

export const HeaderLogo = () => {
  return (
    <Flex align="center" gap={4}>
      <Box
        w="120px"
        minW="120px"
        h="120px"
        borderRadius="full"
        bg="white"
        display="flex"
        alignItems="center"
        justifyContent="center"
        boxShadow="md"
        overflow="hidden"
        hideBelow="md"
      >
        <Image
          src="/logo_blanco.png"
          alt="Logo cultura"
          w="100%"
          h="100%"
          objectFit="cover"
          p={2}
        />
      </Box>
      <Heading fontSize="xl">Cultura ETSII</Heading>
    </Flex>
  );
};
