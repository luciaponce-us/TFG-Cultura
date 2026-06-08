import { VStack, Heading, Flex, Text } from "@chakra-ui/react";
import { CustomButton } from "../components";

export default function NotFoundPage() {
  return (
    <Flex
      bg="background"
      borderRadius="xl"
      boxShadow="lg"
      p={6}
      direction="column"
      align="center"
      justify="center"
      width="fit-content"
      h="fit-content"
      gap={0}
    >
      <VStack gap={4}>
        <Heading as="h1">¡Oops! Página no encontrada</Heading>
        <Text>Lo sentimos, la página que buscas no existe.</Text>
      </VStack>
      <img
        src="/images/character_404.png"
        alt="Not Found"
        style={{ minWidth: 200, width: "100%", maxWidth: 400 }}
      />

      <CustomButton
        onClick={() => (window.location.href = "/")}
        mt={5}
        w="100%"
      >
        Volver al inicio
      </CustomButton>
    </Flex>
  );
}
