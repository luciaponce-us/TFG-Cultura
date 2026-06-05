import { Flex, Heading, VStack, Text, List, Separator } from "@chakra-ui/react";
import { TextSecondary } from "../../components";

export function RulesPage() {
  const h2Style = {
    fontSize: "1.2em",
    fontWeight: "bold",
    marginBottom: "0.5em",
    color: "principal.800",
  };

  function renderList(items: string[]) {
    return (
      <List.Root pl={4}>
        {items.map((item, index) => (
          <List.Item key={index} _marker={{ color: "principal.800" }}>
            {item}
          </List.Item>
        ))}
      </List.Root>
    );
  }

  return (
    <Flex
      bg="background"
      borderRadius="xl"
      boxShadow="lg"
      p={6}
      direction="column"
      align="center"
      gap={4}
      w="800px"
    >
      <Heading as="h1">Normas de Uso</Heading>
      <VStack gap={4} align="flex-start">
        {/* 1 */}
        <Text {...h2Style}>1. Normas del sitio</Text>
        <Text>
          El sitio web Cultura ETSII es un espacio de interacción y colaboración
          entre estudiantes de la Escuela Superior de Ingeniería Informática.
          Para garantizar un ambiente respetuoso y constructivo, se establecen
          las siguientes normas de uso:
        </Text>
        {renderList([
          "Respeta a los demás usuarios. No se toleran insultos, acoso ni amenazas.",
          "No publiques contenido inapropiado, ilegal, ofensivo o fraudulento.",
          "El contenido +18 y/o gore no está permitido.",
          "No hagas spam ni un uso abusivo de la plataforma (incluido el uso de bots o scripts).",
          "No se permite comprar, vender ni hacer publicidad sin autorización.",
          "Usa nombres de usuario y avatares apropiados.",
          "No suplantes identidades ni engañes a otros usuarios.",
          "Evita temas controvertidos como política o religión.",
          "Usa cada sección de la plataforma correctamente.",
          "No promuevas actividades ilegales (piratería, hackeos, etc.).",
          "No está permitido promover, fomentar o incitar conductas autolesivas o dañinas para la salud física o mental.",
          "Sigue las indicaciones de administradores y moderadores.",
          "No fomentes el incumplimiento de las normas.",
          "Es necesario ser estudiante de la Universidad de Sevilla para registrarse.",
          "Las normas pueden cambiar en cualquier momento.",
          "El incumplimiento puede suponer la eliminación de la cuenta o el contenido publicado.",
          "Usa la plataforma con sentido común y respeto.",
          "No crees múltiples cuentas para evadir sanciones.",
          "La plataforma se reserva el derecho de eliminar contenido o cuentas sin previo aviso ni justificación.",
        ])}
        <Text>
          En todo caso, el usuario es responsable de cumplir con las normas
          establecidas y se hace responsable de cualquier acción que realice o
          contenido publicado en la plataforma.
        </Text>
        <Separator variant="solid" color="principal.800" w="100%" />
        <TextSecondary>
          Fecha de última actualización: 5 de junio de 2026
        </TextSecondary>
      </VStack>
    </Flex>
  );
}
