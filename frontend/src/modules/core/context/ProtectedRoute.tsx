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
  const { user, token } = useAuth();
  console.log(
    "ProtectedRoute - Accediendo a ruta protegida con el rol:",
    user?.role,
  );

  // No autenticado
  if (!token) {
    return <Navigate to="/iniciar-sesion" />;
  }

  // Mientras se carga el usuario
  if (!user) {
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

  // No tiene rol suficiente
  if (!allowedRoles.includes(user.role)) {
    return <Navigate to="/no-encontrado" />;
  }

  // Todo correcto
  return children;
}
