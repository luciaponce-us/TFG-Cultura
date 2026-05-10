import {
  Box,
  Flex,
  Heading,
  Image,
  Input,
  InputGroup,
  Menu,
  Portal,
} from "@chakra-ui/react";
import { NavButton } from "./NavButton";
import { IconSearch } from "@tabler/icons-react";

export const Header = () => {
  return (
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
      overflow="hidden"
    >
      <Logo />

      <NavButton to="/">Inicio</NavButton>

      <Flex align="center" gap={4}>
        <SearchBar />
        <ClickableAvatar />
      </Flex>
    </Flex>
  );
};

function Logo() {
  return (
    <Flex align="center" gap={4}>
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
  return (
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

function ClickableAvatar() {
  const links = [
    { title: "Iniciar sesión", href: "/iniciar-sesion" },
    { title: "Registrarse", href: "/registro" },
  ];
  return (
    <Menu.Root>
      <Menu.Trigger asChild>
        <Image
          src="https://res.cloudinary.com/dubz79y98/image/upload/v1776288595/avatar_placeholder_dreac3.png"
          alt="Avatar"
          w="48px"
          h="48px"
          objectFit="cover"
          filter="grayscale(100%)"
        />
      </Menu.Trigger>
      <Portal>
        <Menu.Positioner>
          <Menu.Content
            bg="principal.500"
            borderRadius="md"
            boxShadow="lg"
            p={2}
          >
            {links.map((link) => (
              <Menu.Item
                key={link.href}
                asChild
                value={link.title}
                color="white"
                _highlighted={{ bg: "principal.600" }}
                minH="44px"
                px={3}
              >
                <a href={link.href}>{link.title}</a>
              </Menu.Item>
            ))}
          </Menu.Content>
        </Menu.Positioner>
      </Portal>
    </Menu.Root>
  );
}
