import {
  Box,
  Flex,
  Heading,
  IconButton,
  Image,
  Input,
  InputGroup,
  Drawer,
  useDisclosure,
  useBreakpointValue,
} from "@chakra-ui/react";
import { NavButton } from "../NavButton";
import { IconSearch, IconMenu2, IconX } from "@tabler/icons-react";
import { AvatarMenu } from "./AvatarMenu";
import { MAIN_MENU_LINKS } from "../../config/navigation.config";
import { toaster } from "@/modules/core/components";

export const Header = () => {
  const { open, onOpen, onClose } = useDisclosure();
  const isMobile = useBreakpointValue({ base: true, md: false });

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
        overflow={isMobile ? "visible" : "hidden"}
      >
        {/* HAMBURGER solo móvil */}
        <Flex hideFrom="md" direction="row" gap={2} align="center">
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

        <Logo />

        {/* NAV DESKTOP */}
        <Flex gap={4} hideBelow="md">
          {MAIN_MENU_LINKS.map((link) => (
            <NavButton key={link.href} to={link.href}>
              {link.title}
            </NavButton>
          ))}
        </Flex>
        <Flex align="center" gap={4}>
          <SearchBar />
          <AvatarMenu />
        </Flex>
      </Flex>
      <HamburgerMenu open={open} onOpen={onOpen} onClose={onClose} />
    </>
  );
};

function Logo() {
  return (
    <Flex align="center" gap={4} hideBelow="md">
      <Box
        w="120px"
        minW="120px"
        h="120px"
        borderRadius="full"
        bg="white"
        display="flex"
        alignItems="center"
        justifyContent="center"
        boxShadow="md"
        overflow="hidden"
        hideBelow="md"
      >
        <Image
          src="/logo_blanco.png"
          alt="Logo cultura"
          w="100%"
          h="100%"
          objectFit="cover"
          p={2}
        />
      </Box>
      <Heading fontSize="xl">Cultura ETSII</Heading>
    </Flex>
  );
}

function SearchBar() {
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
    <InputGroup
      endElement={
        <Box color="gray.400">
          <IconSearch size={18} />
        </Box>
      }
      maxW="300px"
    >
      <Input
        placeholder="Buscar..."
        bg="background"
        borderRadius="full"
        px="20px"
        h="40px"
        border="1px solid"
        borderColor="gray.200"
        transition="all 0.2s"
        color="gray.700"
        _hover={{
          borderColor: "gray.300",
        }}
        _focus={{
          outline: "none",
          borderColor: "principal.500",
          boxShadow: "0 0 0 3px rgba(75,117,157,0.15)",
        }}
      />
    </InputGroup>
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
              onClick={onClose}
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
              <IconX style={{ width: 35, height: 35 }} />
            </IconButton>

            <Heading size="xl">Menú</Heading>

            {/* Spacer para centrar el título */}
            <Box w="48px" />
          </Flex>
        </Box>

        <Drawer.Body>
          <Flex direction="column" gap={2}>
            {MAIN_MENU_LINKS.map((link) => (
              <NavButton
                key={link.href}
                to={link.href}
                h="50px"
                onClick={onClose}
              >
                {link.title}
              </NavButton>
            ))}
          </Flex>
        </Drawer.Body>
      </Drawer.Content>
    </Drawer.Root>
  );
}
