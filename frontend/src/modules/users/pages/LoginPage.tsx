import { TextSecondary } from "@/modules/core/components";
import { Flex, Heading, Link, VStack } from "@chakra-ui/react";

export default function LoginPage() {
  return (
    <Flex
      bg="background"
      borderRadius="xl"
      boxShadow="lg"
      p={6}
      direction="column"
      align="center"
      justify="flex-start"
      width="fit-content"
    >
      <VStack gap={4}>
        <Heading as="h1">Iniciar sesión</Heading>
        <TextSecondary>
          ¿Aún no tienes cuenta? Solicita tu registro{" "}
          <Link href="/registro" style={{ textDecoration: "underline" }}>
            aquí
          </Link>
          .
        </TextSecondary>
      </VStack>
    </Flex>
  );
}
