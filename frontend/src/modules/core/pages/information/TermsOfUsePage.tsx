import { Flex, Heading, List, Separator, Text, VStack } from "@chakra-ui/react";
import { TextSecondary } from "../../components";

export function TermsOfUsePage() {
  const h2Style = {
    fontSize: "1.2em",
    fontWeight: "bold",
    marginBottom: "0.5em",
    color: "principal.800",
  };
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
      <Heading as="h1">Términos de Servicio</Heading>
      <VStack gap={4} align="flex-start">
        {/* 1 */}
        <Text {...h2Style}>1. Identificación del titular</Text>
        <Text>
          En cumplimiento con la normativa vigente, se informa que el presente
          sitio web, Cultura ETSII, es titularidad de:
        </Text>

        <List.Root pl={4}>
          <List.Item _marker={{ color: "principal.800" }}>
            Titular: Lucía Ponce García de Sola
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            Email de contacto:{" "}
            <a href="mailto:lucpongar@alum.us.es" style={{ color: "#4B759D", textDecoration: "underline" }}>
              lucpongar@alum.us.es
            </a>
          </List.Item>
        </List.Root>

        {/* 2 */}
        <Text {...h2Style}>2. Objeto</Text>
        <Text>
          El presente sitio web tiene carácter experimental y proporciona
          funcionalidades en desarrollo, incluyendo gestión de usuarios y un
          buzón público de sugerencias.
        </Text>
        <Text>
          El acceso y uso del sitio implica la aceptación de estos términos.
        </Text>

        {/* 3 */}
        <Text {...h2Style}>3. Estado experimental</Text>
        <Text>El usuario acepta que:</Text>
        <List.Root pl={4}>
          <List.Item _marker={{ color: "principal.800" }}>
            El sitio web se encuentra en fase de desarrollo.
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            Puede contener errores, fallos técnicos o interrupciones.
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            No se garantiza la disponibilidad, continuidad ni fiabilidad del
            servicio.
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            Las funcionalidades y los datos almacenados pueden modificarse o
            eliminarse sin previo aviso.
          </List.Item>
        </List.Root>

        {/* 4 */}
        <Text {...h2Style}>4. Registro de usuarios</Text>
        <Text>
          Para acceder a determinadas funcionalidades, el usuario puede
          registrarse en la plataforma.
        </Text>
        <Text>El usuario se compromete a:</Text>
        <List.Root pl={4}>
          <List.Item _marker={{ color: "principal.800" }}>
            Mantener la confidencialidad de sus credenciales.
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            No suplantar la identidad de terceros.
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            No compartir información ilegal, obscena, ofensiva o difamatoria.
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            No utilizar el sitio web para actividades ilícitas o no autorizadas.
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            No utilizar avatares que puedan ser ofensivos o inapropiados
            (desnudos, gore, incitación al odio o al suicidio, etc.).
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            Cumplir con los términos de uso en todo momento.
          </List.Item>
        </List.Root>
        <Text>
          El titular no se responsabiliza del acceso no autorizado derivado de
          negligencia del usuario.
        </Text>

        {/* 5 */}
        <Text {...h2Style}>5. Contenido generado por usuarios</Text>
        <Text>
          El sitio web dispone de un buzón público de sugerencias donde los
          usuarios pueden publicar contenido.
        </Text>
        <Text>El usuario es el único responsable y se compromete a no:</Text>
        <List.Root pl={4}>
          <List.Item _marker={{ color: "principal.800" }}>
            Publicar contenido ilegal, ofensivo o que vulnere derechos de
            terceros.
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            Introducir datos personales propios o de terceros en espacios
            públicos.
          </List.Item>
        </List.Root>
        <Text>
          El titular se reserva el derecho de eliminar cualquier contenido sin
          previo aviso.
        </Text>

        {/* 6 */}
        <Text {...h2Style}>6. Protección de datos</Text>
        <Text>
          El uso del sitio web puede implicar el tratamiento de datos
          personales, especialmente en el registro de usuarios.
        </Text>
        <Text>
          Debido al carácter experimental del sitio, el usuario reconoce que:
        </Text>
        <List.Root pl={4}>
          <List.Item _marker={{ color: "principal.800" }}>
            Las medidas de seguridad pueden no ser definitivas.
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            No se recomienda introducir información sensible.
          </List.Item>
        </List.Root>
        <Text>
          Los datos serán utilizados únicamente para el funcionamiento del
          servicio.
        </Text>

        {/* 7 */}
        <Text {...h2Style}>7. Limitación de responsabilidad</Text>
        <Text>
          El titular no será responsable de ningún daño o perjuicio derivado del
          uso o la imposibilidad de uso del sitio web, incluyendo pero no
          limitado a:
        </Text>
        <List.Root pl={4}>
          <List.Item _marker={{ color: "principal.800" }}>
            Pérdida o corrupción de datos.
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            Fallos de seguridad derivados del estado experimental.
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            Daños derivados del uso del sitio web.
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            Contenidos publicados por los usuarios.
          </List.Item>
        </List.Root>

        {/* 8 */}
        <Text {...h2Style}>8. Suspensión del servicio</Text>
        <Text>
          El titular podrá suspender o interrumpir el servicio en cualquier
          momento sin previo aviso.
        </Text>

        {/* 9 */}
        <Text {...h2Style}>9. Licencia del proyecto </Text>
        <Text>
          Este proyecto está bajo la licencia{" "}
          <a
            href="https://creativecommons.org/licenses/by-nc/4.0/deed.es"
            target="_blank"
            rel="noopener noreferrer"
            style={{ color: "#4B759D", textDecoration: "underline" }}
          >
            Creative Commons Attribution-NonCommercial 4.0 International (CC
            BY-NC 4.0)
          </a>
          . Esto significa que puedes:
        </Text>
        <List.Root pl={4}>
          <List.Item _marker={{ color: "principal.800" }}>
            Usar el proyecto.
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            Modificarlo.
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            Compartirlo.
          </List.Item>
        </List.Root>
        <Text>Siempre que cumplas las siguientes condiciones:</Text>
        <List.Root pl={4}>
          <List.Item _marker={{ color: "principal.800" }}>
            Debes dar crédito a la autora: Lucía Ponce García de Sola.
          </List.Item>
          <List.Item _marker={{ color: "principal.800" }}>
            No puedes utilizar el proyecto con fines comerciales.
          </List.Item>
        </List.Root>

        <Text>
          Puedes consultar los términos completos en el archivo{" "}
          <a
            href="https://github.com/luciaponce-us/TFG-Cultura/blob/develop/LICENSE"
            style={{ color: "#4B759D", textDecoration: "underline" }}
          >
            LICENSE
          </a>
          .
        </Text>

        {/* 10 */}
        <Text {...h2Style}>10. Modificaciones</Text>
        <Text>
          El titular podrá modificar estos términos en cualquier momento sin
          previo aviso.
        </Text>
        <Separator variant="solid" color="principal.800" w="100%"/>
        <TextSecondary>Fecha de última actualización: 4 de junio de 2026</TextSecondary>
      </VStack>
      

    </Flex>
  );
}
