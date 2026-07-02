import { Flex, Heading, HStack, Spinner } from "@chakra-ui/react";
import { useNavigate, useParams } from "react-router-dom";
import { useEffect } from "react";
import { IconArrowNarrowLeft } from "@tabler/icons-react";

import { useAuth } from "@/modules/core/context/useAuth";
import {
  CustomButton,
  TextSecondary,
  toaster,
} from "@/modules/core/components";

import { useUser } from "../hooks";
import { EditUserForm } from "../components";

export function EditUserPage() {
  const { username } = useParams();
  const { token } = useAuth();
  const navigate = useNavigate();

  const {
    data: user,
    isLoading: loadingForm,
    error,
  } = useUser(token, username);

  useEffect(() => {
    if (error) {
      toaster.create({
        title: "Error",
        description: "No se pudo cargar el usuario.",
        type: "error",
      });
    }
  }, [error]);

  let content: React.ReactNode;

  if (loadingForm) {
    content = <Spinner size="xl" borderWidth="4px" color="principal.800" />;
  } else if (user) {
    content = <EditUserForm user={user} />;
  } else {
    content = <TextSecondary>No se encontró el usuario.</TextSecondary>;
  }

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
      <HStack w="100%" justify="space-between" align="center" mb={4}>
        <CustomButton
          color="transparent"
          onClick={() => void navigate("/admin/usuarios")}
        >
          <IconArrowNarrowLeft stroke={2} style={{ width: 32, height: 32 }} />
        </CustomButton>

        <Heading as="h1"> Perfil - {username} </Heading>
        <HStack w="48px" />
      </HStack>
      {content}
    </Flex>
  );
}
