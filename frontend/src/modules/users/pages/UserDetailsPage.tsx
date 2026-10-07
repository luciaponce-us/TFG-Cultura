import { Heading, VStack, Text, HStack, Link } from "@chakra-ui/react";
import {
  useNavigate,
  useParams,
  type NavigateFunction,
} from "react-router-dom";
import {
  IconEye,
  IconFileDollar,
  IconId,
  IconMail,
  IconPhone,
  IconPencil,
  IconTrash,
} from "@tabler/icons-react";
import { useState } from "react";

import { useAuth } from "@/modules/core/context/useAuth";
import {
  CustomAvatar,
  CustomButton,
  ConfirmDialog,
  TextSecondary,
} from "@/modules/core/components";

import { isLowerRole, parsePaymentReceiptUrl, parseRole } from "../utils";
import { PLACEHOLDER } from "@/modules/core/utils/utils";
import { useDeleteUser, useUser } from "../hooks";
import type { Role, User } from "../types";

export function UserDetailsPage() {
  const { username } = useParams<{ username: string }>();
  const { token, user: currentUser } = useAuth();
  const currentUserRole: Role = currentUser?.role ?? "SOCIO";

  const { data: user, isLoading, isError } = useUser(token, username);
  function userHasLowerRole(
    isLoading: boolean,
    user: User | undefined,
    currentUserRole: Role,
  ): boolean {
    if (isLoading || !user || !user.role || !currentUserRole) {
      return true;
    } else {
      return isLowerRole(currentUserRole, user.role);
    }
  }

  const navigate = useNavigate();
  const [deleteDialogOpen, setDeleteDialogOpen] = useState(false);

  function renderAttribute(key: string, value: string, icon: React.ReactNode) {
    return (
      <HStack justify="space-between" w="100%" key={key} gap={6}>
        <HStack color="principal.800">
          {icon}
          <Text fontWeight="bold">{key}</Text>
        </HStack>
        <Text>{value}</Text>
      </HStack>
    );
  }

  const attributes = [
    { key: "DNI", value: user?.dni || "No disponible", icon: <IconId /> },
    { key: "Email", value: user?.email || "No disponible", icon: <IconMail /> },
    {
      key: "Teléfono",
      value: user?.phone || "No disponible",
      icon: <IconPhone />,
    },
  ];

  let content;

  if (isLoading) {
    content = <TextSecondary>Cargando...</TextSecondary>;
  } else if (isError) {
    content = (
      <TextSecondary>
        No se pudo cargar la información del usuario.
      </TextSecondary>
    );
  } else if (!user) {
    content = <TextSecondary>Usuario no encontrado.</TextSecondary>;
  } else {
    content = (
      <VStack gap={6} w="fit-content" maxW="100%" minW={0} overflow="hidden">
        <CustomAvatar
          name={user.name}
          src={user.avatar || PLACEHOLDER.AVATAR}
          size="2xl"
          w="100px"
          h="100px"
        />
        <VStack
          gap={0}
          w="100%"
          maxW="100%"
          minW={0}
          align="center"
          overflow="hidden"
        >
          <Text
            fontSize="lg"
            fontWeight="bold"
            w="100%"
            maxW="100%"
            minW={0}
            display="block"
            textAlign="center"
            overflowWrap="anywhere"
            wordBreak="break-word"
            whiteSpace="normal"
            hyphens="auto"
            flexShrink={1}
          >
            {user.name} {user.surname}
          </Text>
          <Text
            fontSize="md"
            color="gray.500"
            maxW="100%"
            minW={0}
            overflowWrap="anywhere"
            wordBreak="break-word"
          >
            @{user.username}
          </Text>
          <Text
            fontSize="md"
            fontStyle="italic"
            maxW="100%"
            minW={0}
            overflowWrap="anywhere"
            wordBreak="break-word"
          >
            {parseRole(user.role)}
          </Text>
        </VStack>
        <VStack w="fit-content" maxW="100%" minW={0}>
          {attributes.map((attr) =>
            renderAttribute(attr.key, attr.value, attr.icon),
          )}
          <HStack
            justify="space-between"
            w="100%"
            key="payment-receipt"
            gap={6}
            align="center"
          >
            <HStack color="principal.800">
              <IconFileDollar />
              <Text fontWeight="bold">Carta de pago</Text>
            </HStack>

            <CustomButton
              onClick={() =>
                window.open(
                  parsePaymentReceiptUrl(user?.paymentReceipt),
                  "_blank",
                  "noopener,noreferrer",
                )
              }
              size="sm"
            >
              <IconEye stroke={2} /> Ver
            </CustomButton>
          </HStack>
        </VStack>
        {token && !userHasLowerRole(isLoading, user, currentUserRole) && (
          <HStack>
            <CustomButton onClick={() => void navigate("/perfil/editar")}>
              <IconPencil /> Editar
            </CustomButton>
            <CustomButton
              onClick={() => setDeleteDialogOpen(true)}
              color="rojo"
            >
              <IconTrash /> Eliminar
            </CustomButton>
          </HStack>
        )}
      </VStack>
    );
  }

  return (
    <VStack
      bg="background"
      borderRadius="xl"
      boxShadow="lg"
      p={6}
      gap={6}
      w={{ base: "100%", md: "fit-content" }}
      maxW={{ base: "100%", md: "420px" }}
      minW={0}
      overflow="hidden"
      flexShrink={1}
      align="center"
    >
      <Link
        onClick={() => void navigate(-1)}
        color="principal.800"
        alignSelf="start"
      >
        🡰 Volver atrás
      </Link>
      <Heading as="h1" textAlign="center">
        Perfil de @{username}
      </Heading>
      {content}
      {deleteDialogOpen && token && username && (
        <DeleteUserDialog
          isOpen={deleteDialogOpen}
          setIsOpen={setDeleteDialogOpen}
          token={token}
          username={username}
          navigate={navigate}
        />
      )}
    </VStack>
  );
}

function DeleteUserDialog({
  isOpen,
  setIsOpen,
  token,
  username,
  navigate,
}: {
  isOpen: boolean;
  setIsOpen: (isOpen: boolean) => void;
  token: string;
  username: string;
  navigate: NavigateFunction;
}) {
  const { mutateAsync: deleteUser, isPending, isError } = useDeleteUser();

  return (
    <ConfirmDialog
      isOpen={isOpen}
      setIsOpen={setIsOpen}
      handleAction={() => {
        void deleteUser({ token, username });
        if (!isError && !isPending) {
          void navigate(-1);
        }
      }}
      title="Eliminar usuario"
      message={`¿Estás seguro de que deseas eliminar al usuario @${username}? Esta acción es irreversible.`}
    />
  );
}
