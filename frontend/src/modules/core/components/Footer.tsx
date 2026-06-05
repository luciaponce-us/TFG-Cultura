import {
  Box,
  Container,
  SimpleGrid,
  Stack,
  Text,
  Link,
  HStack,
} from "@chakra-ui/react";
import { SocialLink } from "./SocialLink";
import {
  IconBrandInstagram,
  IconBrandX,
  IconBrandTelegram,
  IconBrandDiscord,
  IconMail,
  IconBrandGithub,
  IconMapPin,
} from "@tabler/icons-react";

export const Footer = () => {
  return (
    <Box
      boxShadow="inset 0 6px 10px -6px rgba(0,0,0,0.6)"
      bg="principal.500"
      color="white"
      py={10}
    >
      <Container maxW="6xl">
        <SimpleGrid
          templateColumns={{
            base: "1fr",
            md: "2fr 1fr 1fr 1fr 1fr",
          }}
          gap={8}
        >
          {/* Columna 1 */}
          <Stack>
            <Text fontWeight="bold" fontSize="md">
              Estamos en...
            </Text>
            <HStack alignItems="start" gap={2}>
              <IconMapPin size={30} />
              <Text fontSize="sm">
                Aula AS.42
                <br />
                Escuela Superior de Ingeniería Informática
                <br />
                Universidad de Sevilla
                <br />
                Avda. Reina Mercedes s/n, 41012 Sevilla
              </Text>
            </HStack>
          </Stack>

          {/* Columna 2 */}
          <Stack>
            <Text fontWeight="bold" fontSize="md">
              Redes
            </Text>
            <SocialLink
              label="Instagram"
              href="https://www.instagram.com/cultura_etsii/"
              icon={IconBrandInstagram}
              color="white"
              fontSize="sm"
            />
            <SocialLink
              label="X (Twitter)"
              href="https://x.com/cultura_etsii"
              icon={IconBrandX}
              color="white"
              fontSize="sm"
            />
          </Stack>

          {/* Columna 3 */}
          <Stack>
            <Text fontWeight="bold" fontSize="md">
              Comunidades
            </Text>
            <SocialLink
              label="Telegram"
              href="https://t.me/cultura_etsii"
              icon={IconBrandTelegram}
              color="white"
              fontSize="sm"
            />
            <SocialLink
              label="Discord"
              href="https://discord.gg/cultura_etsii"
              icon={IconBrandDiscord}
              color="white"
              fontSize="sm"
            />
          </Stack>

          {/* Columna 4 */}
          <Stack>
            <Text fontWeight="bold" fontSize="md">
              Contacto
            </Text>
            <SocialLink
              label="Email"
              href="mailto:cultura_etsii@us.es"
              icon={IconMail}
              color="white"
              fontSize="sm"
            />
            <Text fontWeight="bold" fontSize="md">
              Soporte
            </Text>
            <SocialLink
              label="GitHub"
              href="https://github.com/luciaponce-us/TFG-Cultura"
              icon={IconBrandGithub}
              color="white"
              fontSize="sm"
            />
          </Stack>

          {/* Columna 5 */}
          <Stack>
            <Text fontWeight="bold" fontSize="md">
              Legal
            </Text>
            <Link
              href="/politica-de-privacidad"
              target="_blank"
              rel="noopener noreferrer"
              display="flex"
              alignItems="center"
              color="white"
              _hover={{ color: "gray.300" }}
            >
              <Text fontSize="sm">Política de Privacidad</Text>
            </Link>
            <Link
              href="/terminos-de-uso"
              target="_blank"
              rel="noopener noreferrer"
              display="flex"
              alignItems="center"
              color="white"
              _hover={{ color: "gray.300" }}
            >
              <Text fontSize="sm">Términos de Servicio</Text>
            </Link>
          </Stack>
        </SimpleGrid>

        {/* Bottom bar */}
        <Box mt={10} borderTop="1px solid" borderColor="gray.300" pt={6}>
          <Text fontSize="sm" color="gray.300" textAlign="center">
            © {new Date().getFullYear()} Lucía Ponce García de Sola. Proyecto
            bajo licencia CC BY-NC 4.0.
          </Text>
        </Box>
      </Container>
    </Box>
  );
};
