import { Navigate } from "react-router-dom";
import { useAuth } from "./useAuth";
import type { Role } from "../../users/types";
import { Flex, Spinner } from "@chakra-ui/react";

interface ProtectedRouteProps {
  children: React.ReactNode;
  allowedRoles: Role[];
}

export default function ProtectedRoute({
  children,
  allowedRoles,
}: ProtectedRouteProps) {
  const { user, isLoading } = useAuth();
  console.log("Usuario actual:", user, "Cargando:", isLoading);

  // Mientras se carga el usuario
  if (isLoading) {
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
        width="100%"
      >
        <Spinner size="xl" borderWidth="4px" color="principal.800" />
      </Flex>
    );
  }

  // No autenticado
  if (!user) {
    return <Navigate to="/iniciar-sesion" />;
  }

  // No tiene rol suficiente
  if (!allowedRoles.includes(user.role)) {
    return <Navigate to="/no-encontrado" />;
  }

  // Todo correcto
  return children;
}
