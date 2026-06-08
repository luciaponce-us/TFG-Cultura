import { Flex, Heading, List, Separator, Text, VStack } from "@chakra-ui/react";
import { TextSecondary } from "../../components";

export function PrivacyPolicyPage() {
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
      <Heading as="h1">Política de Privacidad</Heading>
      <VStack gap={4} align="flex-start">
        {/* 1 */}
        <Text {...h2Style}>1. Información general</Text>
        <Text>
          La presente Política de Privacidad regula el tratamiento de los datos
          personales recogidos a través de este sitio web (en adelante, "la
          plataforma"). El uso de la plataforma implica la aceptación de esta
          política.
        </Text>
        <Text>
          Este proyecto se encuentra en fase de desarrollo, por lo que pueden
          existir limitaciones en materia de seguridad y estabilidad.
        </Text>
        <Text {...h2Style}>2. Datos recogidos</Text>
        <Text>
          La plataforma podrá recopilar los siguientes datos personales:
        </Text>
        {renderList([
          "Nombre de usuario",
          "Documento de identidad (DNI)",
          "Teléfono",
          "Dirección de correo electrónico",
          "Carta de pago de matrícula de la universidad (formato PDF, subida manual por el usuario, utilizada únicamente para verificar la condición de estudiante)",
          "Nombre de usuario y avatar",
          "Contenido generado por el usuario",
        ])}
        <Text {...h2Style}>3. Finalidad del tratamiento</Text>
        <Text>Los datos serán utilizados con el fin de:</Text>
        {renderList([
          "Gestionar la cuenta del usuario",
          "Permitir el acceso a funcionalidades de la plataforma",
          "Verificar la identidad real de los usuarios",
          "Facilitar la interacción entre usuarios",
          "Mantener el funcionamiento general del servicio",
        ])}
        <Text {...h2Style}>4. Acceso a los datos</Text>
        <Text>
          Los datos personales sensibles (DNI, información de pago, nombre
          completo, teléfono y correo electrónico) serán accesibles únicamente
          por:
        </Text>
        {renderList([
          "El propio usuario titular de los datos",
          "Los administradores de la plataforma",
        ])}
        <Text>
          No obstante, en caso de brecha de seguridad, estos datos podrían verse
          comprometidos.
        </Text>
        <Text>
          El nombre de usuario y el avatar serán públicos dentro de la
          plataforma.
        </Text>

        <Text {...h2Style}>5. Contenido generado por el usuario</Text>
        <Text>
          Todo contenido generado por los usuarios será público por defecto.
        </Text>
        <Text>
          La plataforma se reserva el derecho de eliminar cualquier contenido en
          cualquier momento, sin previo aviso.
        </Text>
        <Text {...h2Style}>6. Medidas de seguridad</Text>
        <Text>
          Aunque se aplican medidas para proteger la información, la plataforma
          se encuentra en desarrollo, por lo que:
        </Text>
        {renderList([
          "Las medidas de seguridad pueden no ser definitivas",
          "No se garantiza la seguridad absoluta de los datos",
          "Pueden existir vulnerabilidades o fallos de seguridad",
        ])}

        <Text>El titular de la plataforma no se responsabiliza de:</Text>
        {renderList([
          "Acceso no autorizado a datos personales",
          "Pérdida, daño o modificación de datos",
          "Cualquier daño o perjuicio derivado del uso de la plataforma",
          "Uso malintencionado de la información por parte de terceros",
        ])}
        <Text {...h2Style}>7. Responsabilidad</Text>
        <Text>
          El uso de la plataforma se realiza bajo la responsabilidad del
          usuario. El titular no será responsable de daños derivados de:
        </Text>
        {renderList([
          "Uso indebido de la plataforma por parte del usuario",
          "Publicación de contenido ilegal, ofensivo o que vulnere derechos de terceros",
          "Introducción de datos personales propios o de terceros en espacios públicos",
          "Acceso no autorizado a datos personales",
          "Fallas de seguridad o vulnerabilidades en la plataforma",
          "Cualquier daño o perjuicio derivado del uso de la plataforma",
          "Uso malintencionado de la información por parte de terceros",
          "Interrupciones o fallos en el servicio debido a mantenimiento, actualizaciones o problemas técnicos",
        ])}
        <Text {...h2Style}>8. Cambios en la política</Text>
        <Text>
          La presente Política de Privacidad podrá modificarse en cualquier
          momento:
        </Text>
        {renderList([
          "Sin previo aviso",
          "Sin necesidad de consentimiento por parte de los usuarios",
          "Siendo efectiva desde su publicación en la plataforma",
        ])}
        <Text>
          Se recomienda revisar esta política periódicamente para estar
          informado sobre cualquier cambio.
        </Text>
        <Text {...h2Style}>9. Aceptación de la política</Text>
        <Text>
          El uso de la plataforma implica la aceptación de esta Política de
          Privacidad. Si el usuario no está de acuerdo con alguna parte de esta
          política, se recomienda no utilizar la plataforma y eliminar su
          cuenta.
        </Text>
        <Separator variant="solid" color="principal.800" w="100%" />
        <TextSecondary>
          Fecha de última actualización: 5 de junio de 2026
        </TextSecondary>
      </VStack>
    </Flex>
  );
}
