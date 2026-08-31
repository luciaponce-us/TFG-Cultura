import { useState } from "react";
import {
  CATALOG_SUBMENU_LINKS,
  MAIN_MENU_LINKS,
} from "../../config/navigation.config";
import { NavButton } from "@/modules/core/components";
import {
  Box,
  Drawer,
  Flex,
  Heading,
  IconButton,
  useDisclosure,
} from "@chakra-ui/react";
import { IconChevronLeft, IconMenu2, IconX } from "@tabler/icons-react";
import { AvatarMenu, HeaderSearchBar } from "./";

export function HeaderMobile() {
  const { open, onOpen, onClose } = useDisclosure();

  return (
    <>
    <Flex
        as="header"
        justify="space-between"
        align="center"
        px={6}
        py={0}
        bg="principal.500"
        color="white"
        shadow="card"
        h="80px"
        overflow="visible"
      >
      <Flex direction="row" gap={2} align="center">
        <IconButton
          aria-label="Abrir menú"
          onClick={onOpen}
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
          <IconMenu2 style={{ width: 40, height: 40 }} />
        </IconButton>
        <Heading fontSize="xl">Cultura ETSII</Heading>
        
      </Flex>
      <Flex align="center" gap={4}>
                  <HeaderSearchBar />
                  <AvatarMenu />
                </Flex>
      </Flex>
      <HamburgerMenu open={open} onOpen={onOpen} onClose={onClose} />
    </>
  );
}

function HamburgerMenu({
  open,
  onOpen,
  onClose,
}: {
  open: boolean;
  onOpen: () => void;
  onClose: () => void;
}) {
  const [catalogOpen, setCatalogOpen] = useState(false);

  const mainMenu = (
    <>
      {MAIN_MENU_LINKS.map((link) => (
        <NavButton key={link.href} to={link.href} h="50px" onClick={onClose}>
          {link.title}
        </NavButton>
      ))}

      <NavButton
        h="50px"
        onClick={() => {
          setCatalogOpen(true);
        }}
      >
        Catálogo
      </NavButton>
    </>
  );

  const catalogMenu = (
    <>
      {CATALOG_SUBMENU_LINKS.map((link) => (
        <NavButton
          key={link.href}
          to={link.href}
          h="50px"
          onClick={() => {
            setCatalogOpen(false);
            onClose();
          }}
        >
          {link.title}
        </NavButton>
      ))}

      <NavButton h="50px" onClick={() => setCatalogOpen(false)}>
        Volver al menú principal
      </NavButton>
    </>
  );

  return (
    <Drawer.Root
      open={open}
      onOpenChange={(e) => (e.open ? onOpen() : onClose())}
      placement="top"
    >
      <Drawer.Content
        bg="principal.500"
        color="white"
        w="100vw"
        h="100vh"
        position="fixed"
        gap={2}
        p={2}
      >
        <Box
          px={4}
          py={3}
          borderBottom="1px solid"
          borderColor="whiteAlpha.300"
        >
          <Flex align="center" justify="space-between">
            <IconButton
              aria-label="Cerrar menú"
              onClick={catalogOpen ? () => setCatalogOpen(false) : onClose}
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
              {catalogOpen ? (
                <IconChevronLeft style={{ width: 35, height: 35 }} />
              ) : (
                <IconX style={{ width: 35, height: 35 }} />
              )}
            </IconButton>

            <Heading size="xl">{catalogOpen ? "Catálogo" : "Menú"}</Heading>

            {/* Spacer para centrar el título */}
            <Box w="48px" />
          </Flex>
        </Box>

        <Drawer.Body>
          <Flex direction="column" gap={2}>
            {catalogOpen ? catalogMenu : mainMenu}
          </Flex>
        </Drawer.Body>
      </Drawer.Content>
    </Drawer.Root>
  );
}
