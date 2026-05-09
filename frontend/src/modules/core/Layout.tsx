import { Flex } from "@chakra-ui/react";
import { Outlet } from "react-router-dom";
import { Header, Footer } from "./components";

export default function Layout() {
  return (
    <Flex minH="100vh" direction="column">
      <Header />

      <Flex
        as="main"
        p={6}
        px={12}
        direction="column"
        gap={6}
        flex="1"
        align="center"
      >
        <Outlet />
      </Flex>

      <Footer />
    </Flex>
  );
}
