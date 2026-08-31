import { Flex } from "@chakra-ui/react";
import {
  AvatarMenu,
  HeaderLogo,
  HeaderSearchBar,
} from "@/modules/core/components/header";
import { useNavigate } from "react-router-dom";
import { useEffect, useRef, useState } from "react";
import { NavButton, CustomButton } from "@/modules/core/components";
import {
  CATALOG_SUBMENU_LINKS,
  MAIN_MENU_LINKS,
} from "../../config/navigation.config";
import { IconX } from "@tabler/icons-react";

export function HeaderDesktop() {
  const [catalogOpen, setCatalogOpen] = useState(false);

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
        overflow="hidden"
      >
        <HeaderLogo />

        {/* NAV DESKTOP */}
        <Flex gap={4} hideBelow="md">
          {MAIN_MENU_LINKS.map((link) => (
            <NavButton key={link.href} to={link.href}>
              {link.title}
            </NavButton>
          ))}

          <NavButton to="/catalogo" onMouseEnter={() => setCatalogOpen(true)}>
            Catálogo
          </NavButton>
        </Flex>
        <Flex align="center" gap={4}>
          <HeaderSearchBar />
          <AvatarMenu />
        </Flex>
      </Flex>
      <CatalogMenu catalogOpen={catalogOpen} setCatalogOpen={setCatalogOpen} />
    </>
  );
}

function CatalogMenu({
  catalogOpen,
  setCatalogOpen,
}: {
  catalogOpen: boolean;
  setCatalogOpen: (open: boolean) => void;
}) {
  const navigate = useNavigate();
  const catalogMenuRef = useRef<HTMLDivElement>(null);

  useEffect(() => {
    const handleClickOutside = (event: MouseEvent) => {
      if (
        catalogMenuRef.current &&
        !catalogMenuRef.current.contains(event.target as Node)
      ) {
        setCatalogOpen(false);
      }
    };

    document.addEventListener("mousedown", handleClickOutside);

    return () => {
      document.removeEventListener("mousedown", handleClickOutside);
    };
  }, [setCatalogOpen]);

  return (
    <div ref={catalogMenuRef} style={{width: "100%"}}>
      <Flex
        as="nav"
        aria-label="Submenú del catálogo"
        hidden={!catalogOpen}
        bg="principal.600"
        borderTop="1px solid"
        borderColor="whiteAlpha.300"
        px={{ base: 3, md: 6 }}
        py={2}
        w="100%"
      >
        <Flex
          align="center"
          justify="center"
          w="100%"
          position="relative"
        >
          <Flex
            gap={2}
            wrap="nowrap"
            overflowX="auto"
            justify="center"
            align="center"
            css={{
              scrollbarWidth: "none",
              "&::-webkit-scrollbar": {
                display: "none",
              },
            }}
            flex="1"
            mx={10}
          >
            {CATALOG_SUBMENU_LINKS.map((link) => (
              <CustomButton
                key={link.href}
                color="transparent"
                style={{ color: "white" }}
                onClick={() => {
                  void navigate(link.href);
                  setCatalogOpen(false);
                }}
              >
                {link.title}
              </CustomButton>
            ))}
          </Flex>

          <CustomButton
            onClick={() => {
              setCatalogOpen(false);
            }}
            color="transparent"
            style={{ color: "white" }}
            position="absolute"
            right={0}
            aria-label="Cerrar submenú del catálogo"
          >
            <IconX />
          </CustomButton>
        </Flex>
      </Flex>
    </div>
  );
}
