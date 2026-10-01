import { IconButton, useBreakpointValue } from "@chakra-ui/react";
import { CustomSearchBar, toaster } from "@/modules/core/components";
import { IconSearch } from "@tabler/icons-react";

export const HeaderSearchBar = () => {
  const isMobile = useBreakpointValue({ base: true, md: false });

  return isMobile ? (
    <IconButton
      aria-label="Abrir menú"
      onClick={() => {
        toaster.create({
          title: "Funcionalidad no implementada",
          description: "Por el momento, no se pueden realizar búsquedas",
        });
      }}
      variant="ghost"
      color="white"
      _hover={{
        bg: "principal.600",
      }}
      _active={{
        bg: "principal.700",
        transform: "scale(0.92)",
      }}
      boxSize="48px"
    >
      <IconSearch style={{ width: 35, height: 35 }} />
    </IconButton>
  ) : (
    <CustomSearchBar
      background="background"
      onChange={() => {
        toaster.create({
          title: "Funcionalidad no implementada",
          description: "Por el momento, no se pueden realizar búsquedas",
        });
      }}
    />
  );
};
