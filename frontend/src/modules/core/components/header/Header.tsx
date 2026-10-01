import { useBreakpointValue } from "@chakra-ui/react";
import { HeaderMobile, HeaderDesktop } from "./";

export const Header = () => {
  const isMobile = useBreakpointValue({ base: true, md: false });

  const content = isMobile ? <HeaderMobile /> : <HeaderDesktop />;

  return content;
};
