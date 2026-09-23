import { Flex, Grid, GridItem, Heading, VStack } from "@chakra-ui/react";
import { CustomButton, toaster } from "../components";
import {
  IconUsers,
  IconBox,
  IconAlertTriangle,
  IconStar,
  IconChartPie4,
  IconFolders,
} from "@tabler/icons-react";
import type { ReactNode } from "react";
import { useNavigate } from "react-router-dom";

interface AdminCardProps {
  icon: ReactNode;
  label: string;
  onClick: () => void;
}

function AdminCard({ icon, label, onClick }: AdminCardProps) {
  return (
    <GridItem
      onClick={onClick}
      cursor="pointer"
      _hover={{ color: "principal.600" }}
      _active={{ color: "principal.700", transform: "scale(0.98)" }}
      transition="all 0.2s"
      minW={0}
      maxW="300px"
      w="100%"
    >
      <VStack align="center" justify="center" h="100%">
        {icon}
        <CustomButton onClick={onClick}>{label}</CustomButton>
      </VStack>
    </GridItem>
  );
}

export default function AdminPanelPage() {
  const navigation = useNavigate();
  const adminCardIconStyle = {
    minWidth: "60px",
    minHeight: "60px",
    width: "80%",
    height: "80%",
    padding: "10px",
  };

  const adminLinks = [
    {
      icon: <IconUsers style={adminCardIconStyle} stroke={1.5} />,
      label: "Usuarios",
      onClick: () => void navigation("/admin/usuarios"),
    },
    {
      icon: <IconBox style={adminCardIconStyle} stroke={1.5} />,
      label: "Préstamos",
      onClick: () =>
        toaster.create({
          title: "Préstamos",
          description: "Funcionalidad en desarrollo",
        }),
    },
    {
      icon: <IconAlertTriangle style={adminCardIconStyle} stroke={1.5} />,
      label: "Incidencias",
      onClick: () =>
        toaster.create({
          title: "Incidencias",
          description: "Funcionalidad en desarrollo",
        }),
    },
    {
      icon: <IconFolders style={adminCardIconStyle} stroke={1.5} />,
      label: "Sagas",
      onClick: () => void navigation("/admin/sagas"),
    },
    {
      icon: <IconStar style={adminCardIconStyle} stroke={1.5} />,
      label: "Categorías",
      onClick: () => void navigation("/admin/categorias"),
    },
    {
      icon: <IconChartPie4 style={adminCardIconStyle} stroke={1.5} />,
      label: "Secciones",
      onClick: () =>
        toaster.create({
          title: "Secciones",
          description: "Funcionalidad en desarrollo",
        }),
    },
  ];

  return (
    <Flex
      bg="background"
      borderRadius="xl"
      boxShadow="lg"
      p={6}
      direction="column"
      align="center"
      justify="center"
      flex={1}
      h="fit-content"
      gap={6}
      w="60vw"
    >
      <Heading as="h1" textAlign="center">
        Panel de administración
      </Heading>
      <Grid
        templateColumns={{ base: "repeat(2, 1fr)", md: "repeat(3, 1fr)" }}
        templateRows="repeat(2, 1fr)"
        gap={8}
        color="principal.500"
        flex={1}
        w="100%"
        h="fit-content"
        alignItems="center"
        justifyItems="center"
      >
        {adminLinks.map((link, index) => (
          <AdminCard
            key={index}
            icon={link.icon}
            label={link.label}
            onClick={link.onClick}
          />
        ))}
      </Grid>
    </Flex>
  );
}
