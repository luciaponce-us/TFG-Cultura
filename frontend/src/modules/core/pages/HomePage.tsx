import { Flex, Grid, Heading, Text, VStack } from "@chakra-ui/react";
import {
  IconBrandInstagram,
  IconBrandX,
  IconBrandTelegram,
  IconBrandDiscord,
} from "@tabler/icons-react";
import { useEffect, useState } from "react";
import { fetchDummyData, fetchMongoData } from "../service/dummy.service";
import { CustomAlert, SocialLink, SideBar } from "../components";

export default function Home() {
  return (
    <Grid
      templateColumns={{ base: "1fr", md: "1fr 4fr 1fr" }}
      gap={10}
      flex={1}
    >
      {/* IZQUIERDA */}
      <SideBar>
        <VStack align="start" gap={4}>
          <Heading as="h1">Redes sociales</Heading>
          <VStack align="start" gap={2}>
            <SocialLink
              label="Instagram"
              href="https://www.instagram.com/cultura_etsii/"
              icon={IconBrandInstagram}
            />
            <SocialLink
              label="X (Twitter)"
              href="https://x.com/cultura_etsii"
              icon={IconBrandX}
            />
          </VStack>
        </VStack>
        <VStack align="start" gap={4}>
          <Heading as="h1">Comunidades</Heading>
          <VStack align="start" gap={2}>
            <SocialLink
              label="Telegram"
              href="https://t.me/+SgtDKRourfMxOWQ0#"
              icon={IconBrandTelegram}
            />
            <SocialLink
              label="Discord"
              href="https://discord.com/invite/WfGPGUTZJP"
              icon={IconBrandDiscord}
            />
          </VStack>
        </VStack>
      </SideBar>

      {/* CENTRO (más ancho) */}
      <Flex
        bg="background"
        borderRadius="xl"
        boxShadow="lg"
        p={6}
        direction="column"
        align="center"
        justify="flex-start"
        gap={6}
      >
        <Heading as="h1">Bienvenidos a Cultura ETSII</Heading>
        <Alerts />
      </Flex>

      {/* DERECHA */}
      <SideBar>
        <VStack align="start" gap={4}>
          <Heading as="h1">Destacados</Heading>
          <Text>Próximamente...</Text>
        </VStack>
      </SideBar>
    </Grid>
  );
}

function Alerts() {
  const [backendState, setBackendState] = useState<
    "loading" | "success" | "error"
  >("loading");
  const [backendMessage, setBackendMessage] = useState<string>(
    "Comprobando conexión con el backend...",
  );
  const [mongoState, setMongoState] = useState<"loading" | "success" | "error">(
    "loading",
  );
  const [mongoMessage, setMongoMessage] = useState<string>(
    "Comprobando conexión con la base de datos...",
  );

  useEffect(() => {
    const loadDummyData = async () => {
      try {
        await fetchDummyData();
      } catch (error) {
        setBackendState("error");
        setBackendMessage(
          "No se pudo conectar con el backend. ERROR: " + error,
        );
        return;
      }
      setBackendState("success");
      setBackendMessage("Conexión con el backend establecida.");
    };

    const loadMongoData = async () => {
      try {
        await fetchMongoData();
      } catch (error) {
        setMongoState("error");
        setMongoMessage(
          "No se pudo conectar con la base de datos. ERROR: " + error,
        );
        return;
      }
      setMongoState("success");
      setMongoMessage("Conexión con la base de datos establecida.");
    };

    loadDummyData();
    loadMongoData();
  }, []);

  return (
    <VStack gap="4" width="full">
      <CustomAlert state={backendState} message={backendMessage} />
      <CustomAlert state={mongoState} message={mongoMessage} />
    </VStack>
  );
}
