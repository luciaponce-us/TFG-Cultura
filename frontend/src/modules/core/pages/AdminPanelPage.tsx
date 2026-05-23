import { Flex, Grid, GridItem, Heading, VStack } from "@chakra-ui/react";
import { CustomButton, SideBar } from "../components";
import {
  IconUsers,
  IconBox,
  IconAlertTriangle,
  IconChartBar,
  IconStar,
  IconChartPie4,
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
    <GridItem>
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
      onClick: () => navigation("/admin/usuarios"),
    },
    {
      icon: <IconBox style={adminCardIconStyle} stroke={1.5} />,
      label: "Préstamos",
      onClick: () => navigation("/admin/prestamos"),
    },
    {
      icon: <IconAlertTriangle style={adminCardIconStyle} stroke={1.5} />,
      label: "Incidencias",
      onClick: () => console.log("Incidencias"),
    },
    {
      icon: <IconChartBar style={adminCardIconStyle} stroke={1.5} />,
      label: "Estadísticas",
      onClick: () => console.log("Estadísticas"),
    },
    {
      icon: <IconStar style={adminCardIconStyle} stroke={1.5} />,
      label: "Reseñas",
      onClick: () => console.log("Reseñas"),
    },
    {
      icon: <IconChartPie4 style={adminCardIconStyle} stroke={1.5} />,
      label: "Secciones",
      onClick: () => console.log("Secciones"),
    },
  ];

  return (
    <Grid
      templateColumns={{ base: "1fr", md: "1fr 3fr" }}
      gap={{ base: 8, md: 10 }}
      flex={{ base: "initial", md: 1 }}
    >
      {/* IZQUIERDA */}
      <SideBar order={{ base: 2, md: 1 }} hideOnMobile={false}>
        <VStack align="start" gap={0}>
          <Heading as="h1">Actividad reciente</Heading>
          <VStack align="start" gap={2}></VStack>
        </VStack>
      </SideBar>

      {/* CENTRO */}
      <Flex
        bg="background"
        borderRadius="xl"
        boxShadow="lg"
        p={6}
        direction="column"
        align="center"
        justify="flex-start"
        flex={1}
        h="fit-content"
        order={{ base: 1, md: 2 }}
      >
        <Heading as="h1">Panel de administración</Heading>
        <Grid
          templateColumns="repeat(3, 1fr)"
          templateRows="repeat(2, 1fr)"
          gap={8}
          color="principal.500"
          flex={1}
          w="100%"
          h="100%"
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
    </Grid>
  );
}
