import { useState } from "react";
import { useAuth } from "../../context/useAuth";
import { useNavigate } from "react-router-dom";
import {
  Menu,
  Drawer,
  Box,
  Flex,
  Heading,
  Portal,
  IconButton,
  useBreakpointValue,
  Button,
} from "@chakra-ui/react";
import { CustomAvatar, NavButton } from "../";
import { IconLogout, IconX } from "@tabler/icons-react";
import type { User } from "@/modules/users/types";
import { toaster } from "../toaster/toaster";
import { getUserLinks, type NavLink } from "../../config/navigation.config";

export function AvatarMenu() {
  const { user } = useAuth();
  const { logout } = useAuth();
  const isMobile = useBreakpointValue({ base: true, md: false });
  const [open, setOpen] = useState(false);

  const links: NavLink[] = getUserLinks(user);

  return isMobile ? (
    <AvatarDrawerMenu
      open={open}
      onOpen={() => setOpen(true)}
      onClose={() => setOpen(false)}
      user={user}
      links={links}
      logout={logout}
    />
  ) : (
    <AvatarDropdownMenu
      user={user}
      links={links}
      logout={logout}
    />
  );
}

function TriggerAvatar({ user }: { user: User | null | undefined }) {
  return (
    <Box
      cursor="pointer"
      borderRadius="full"
      transition="all 0.2s"
      _hover={{
        transform: "scale(1.05)",
        boxShadow: "md",
      }}
      _active={{
        transform: "scale(0.95)",
        boxShadow: "sm",
      }}
    >
      <CustomAvatar
        name={user?.username + "s avatar"}
        src={
          user?.avatar ||
          "https://res.cloudinary.com/dubz79y98/image/upload/v1776288595/avatar_placeholder_dreac3.png"
        }
        w="48px"
        h="48px"
        filter={user ? undefined : "grayscale(100%)"}
      />
    </Box>
  );
}

function AvatarDropdownMenu({
  user,
  links,
  logout
}: {
  user: User | null | undefined;
  links: NavLink[];
  logout: () => void;
}) {
  const navigate = useNavigate();
  return (
    <Menu.Root>
      <Menu.Trigger>
        <TriggerAvatar user={user} />
      </Menu.Trigger>
      <Portal>
        <Menu.Positioner>
          <Menu.Content
            bg="principal.500"
            borderRadius="md"
            boxShadow="lg"
            p={2}
          >
            {links.map((link: NavLink) => (
              <Menu.Item
                key={link.href}
                asChild
                value={link.title}
                color="white"
                _highlighted={{ bg: "principal.600" }}
                minH="44px"
                px={3}
                w="100%"
                cursor="pointer"
              >
                <a
                  href={link.href}
                  style={{
                    display: "flex",
                    alignItems: "center",
                    gap: 8,
                    width: "100%",
                  }}
                >
                  {link.icon && (
                    <span
                      style={{ display: "inline-flex", alignItems: "center" }}
                    >
                      {link.icon}
                    </span>
                  )}
                  <span>{link.title}</span>
                </a>
              </Menu.Item>
            ))}
            {user && (
              <Menu.Item
                value="Cerrar sesión"
                color="white"
                _highlighted={{ bg: "principal.600" }}
                minH="44px"
                px={3}
                cursor="pointer"
                onSelect={() => {
                  logout();
                  toaster.create({
                    title: "Sesión cerrada exitosamente",
                    description: "¡Hasta pronto! 👋​",
                    type: "info",
                  });
                  void navigate("/");
                }}
              >
                <IconLogout />
                Cerrar sesión
              </Menu.Item>
            )}
          </Menu.Content>
        </Menu.Positioner>
      </Portal>
    </Menu.Root>
  );
}

function AvatarDrawerMenu({
  open,
  onOpen,
  onClose,
  user,
  links,
  logout
}: {
  open: boolean;
  onOpen: () => void;
  onClose: () => void;
  user: User | null | undefined;
  links: NavLink[];
  logout: () => void;
}) {
  const navigate = useNavigate();
  return (
    <Drawer.Root
      open={open}
      placement="top"
      onOpenChange={(e) => (e.open ? onOpen() : onClose())}
    >
      <Drawer.Trigger>
        <TriggerAvatar user={user} />
      </Drawer.Trigger>
      <Drawer.Backdrop />

      <Drawer.Content
        bg="principal.500"
        color="white"
        w="100vw"
        h="100vh"
        position="fixed"
        top={0}
        left={0}
      >
        {/* HEADER */}
        <Box
          px={4}
          py={3}
          borderBottom="1px solid"
          borderColor="whiteAlpha.300"
        >
          <Flex align="center" justify="space-between">
            {/* Spacer para centrar el título */}
            <Box w="48px" />
            <Heading size="xl">Cuenta</Heading>
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
          </Flex>
        </Box>

        <Drawer.Body>
          <Flex direction="column" justify="space-between" h="100%">
            <Flex direction="column" gap={2}>
              {links.map((link: NavLink) => (
                <NavButton
                  key={link.href}
                  to={link.href}
                  onClick={onClose}
                  w="100%"
                  h="50px"
                >
                  {link.icon && (
                    <span
                      style={{ display: "inline-flex", alignItems: "center" }}
                    >
                      {link.icon}
                    </span>
                  )}
                  <span>{link.title}</span>
                </NavButton>
              ))}
            </Flex>
            <Box>
              {user && (
                <Button
                  variant="ghost"
                  color="white"
                  _hover={{
                    bg: "principal.600",
                  }}
                  _active={{
                    bg: "principal.700",
                    transform: "scale(0.92)",
                  }}
                  fontSize="lg"
                  onClick={() => {
                    onClose();
                    logout();
                    toaster.create({
                      title: "Sesión cerrada exitosamente",
                      description: "¡Hasta pronto! 👋​",
                      type: "info",
                    });
                    void navigate("/");
                  }}
                  w="100%"
                  h="50px"
                >
                  <IconLogout /> Cerrar sesión
                </Button>
              )}
            </Box>
          </Flex>
        </Drawer.Body>
      </Drawer.Content>
    </Drawer.Root>
  );
}
