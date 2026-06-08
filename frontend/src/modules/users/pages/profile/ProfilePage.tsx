import { useAuth } from "@/modules/core/context/useAuth";
import { useNavigate } from "react-router-dom";
import { Heading, VStack, Text, HStack, Dialog } from "@chakra-ui/react";
import { TextSecondary } from "@/modules/core/components/text/TextSecondary";
import { CustomAvatar, CustomButton, toaster } from "@/modules/core/components";
import { parsePaymentReceiptUrl, parseRole } from "../../utils";
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
import { deleteMyAccount } from "../../service/user.service";

export function ProfilePage() {
  const { user } = useAuth();
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

  return (
    <VStack bg="background" borderRadius="xl" boxShadow="lg" p={6} gap={6}>
      <Heading as="h1">Mi perfil</Heading>
      {user ? (
        <VStack gap={6}>
          <CustomAvatar
            name={user.name}
            src={user.avatar}
            size="2xl"
            w="100px"
            h="100px"
          />
          <VStack gap={0}>
            <Text fontSize="lg" fontWeight="bold">
              {user.name} {user.surname}
            </Text>
            <Text fontSize="md" color="gray.500">
              @{user.username}
            </Text>
            <Text fontSize="md" fontStyle="italic">
              {parseRole(user.role)}
            </Text>
          </VStack>
          <VStack>
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
          <HStack>
            <CustomButton onClick={() => navigate("/perfil/editar")}>
              <IconPencil /> Editar
            </CustomButton>
            <CustomButton
              onClick={() => setDeleteDialogOpen(true)}
              color="rojo"
            >
              <IconTrash /> Eliminar
            </CustomButton>
          </HStack>
        </VStack>
      ) : (
        <TextSecondary>No se ha podido cargar el usuario.</TextSecondary>
      )}
      <DeleteModal
        deleteDialogOpen={deleteDialogOpen}
        setDeleteDialogOpen={setDeleteDialogOpen}
      />
    </VStack>
  );
}

function DeleteModal({
  deleteDialogOpen,
  setDeleteDialogOpen,
}: {
  deleteDialogOpen: boolean;
  setDeleteDialogOpen: (open: boolean) => void;
}) {
  const [loading, setLoading] = useState(false);
  const { token, logout } = useAuth();
  const navigate = useNavigate();

  async function handleDelete() {
    setLoading(true);
    try {
      await deleteMyAccount(token!);
      toaster.create({
        title: "Cuenta eliminada",
        description: "Tu cuenta ha sido eliminada exitosamente.",
        type: "success",
      });
      logout();
      navigate("/iniciar-sesion");
    } catch (error) {
      console.error("Error al eliminar la cuenta:", error);
      toaster.create({
        title: "Error",
        description:
          "No se pudo eliminar la cuenta. Por favor, intenta nuevamente.",
        type: "error",
      });
    } finally {
      setLoading(false);
    }
  }

  return (
    <Dialog.Root
      open={deleteDialogOpen}
      onOpenChange={() => setDeleteDialogOpen(false)}
    >
      <Dialog.Backdrop />
      <Dialog.Positioner>
        <Dialog.Content
          maxH="80vh"
          overflow="hidden"
          borderRadius="xl"
          bg="background"
        >
          <Dialog.CloseTrigger />
          <Dialog.Header>
            <Dialog.Title>
              <Heading as="h1">Eliminar cuenta</Heading>
            </Dialog.Title>
          </Dialog.Header>
          <Dialog.Body>
            <VStack>
              <Text>
                ¿Estás seguro de que deseas eliminar tu cuenta? Esta acción es
                irreversible.
              </Text>
            </VStack>
          </Dialog.Body>
          <Dialog.Footer>
            <CustomButton
              onClick={() => setDeleteDialogOpen(false)}
              color="rojo"
            >
              Cancelar
            </CustomButton>
            <CustomButton onClick={handleDelete} loading={loading}>
              Confirmar
            </CustomButton>
          </Dialog.Footer>
        </Dialog.Content>
      </Dialog.Positioner>
    </Dialog.Root>
  );
}
