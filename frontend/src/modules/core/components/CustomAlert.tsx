import { Alert, CloseButton, Spinner } from "@chakra-ui/react";
import { useState } from "react";

type BackendStatus = "loading" | "success" | "error";
type Props = {
  state: BackendStatus;
  message?: string;
  closeable?: boolean;
};

export const CustomAlert = ({ state, message, closeable = true }: Props) => {
  const [visible, setVisible] = useState(true);
  if (!visible) return null;

  if (state === "loading") {
    return (
      <Alert.Root
        alignItems="center"
        justifyContent="space-between"
        title="Cargando..."
      >
        <Alert.Indicator>
          <Spinner size="sm" />
        </Alert.Indicator>

        <Alert.Content>{message ?? "Cargando..."}</Alert.Content>
      </Alert.Root>
    );
  }

  if (state === "error") {
    return (
      <Alert.Root
        status="error"
        alignItems="center"
        justifyContent={closeable? "space-between":""}
        title="Error"
      >
        <Alert.Indicator />

        <Alert.Title>{message ?? "Ha ocurrido un error"}</Alert.Title>
        {closeable && (
          <CloseButton
            pos="relative"
            top="-2"
            insetEnd="-2"
            onClick={() => setVisible(false)}
          />
        )}
      </Alert.Root>
    );
  }

  return (
    <Alert.Root
      status="success"
      alignItems="center"
      justifyContent="space-between"
      title="Éxito"
    >
      <Alert.Indicator />

      <Alert.Title>{message ?? "Operación completada con éxito."}</Alert.Title>

      <CloseButton
        pos="relative"
        top="-2"
        insetEnd="-2"
        onClick={() => setVisible(false)}
      />
    </Alert.Root>
  );
};
