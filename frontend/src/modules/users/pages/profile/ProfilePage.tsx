import { useAuth } from "@/modules/core/context/useAuth";
import { Heading, VStack, Text, HStack } from "@chakra-ui/react";
import { TextSecondary } from "@/modules/core/components/text/TextSecondary";
import { CustomAvatar, CustomButton } from "@/modules/core/components";
import { parsePaymentReceiptUrl, parseRole } from "../../utils";
import {
  IconEye,
  IconFileDollar,
  IconId,
  IconMail,
  IconPhone,
} from "@tabler/icons-react";

export function ProfilePage() {
  const { user } = useAuth();
  
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
    <VStack
      bg="background"
      borderRadius="xl"
      boxShadow="lg"
      p={6}
      gap={6}
    >
      <Heading as="h1">Mi perfil</Heading>
      {user ? (
        <VStack gap={6}>
          <CustomAvatar name={user.name} src={user.avatar} size="2xl" w="100px" h="100px" />
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
        </VStack>
      ) : (
        <TextSecondary>No se ha podido cargar el usuario.</TextSecondary>
      )}
    </VStack>
  );
}
