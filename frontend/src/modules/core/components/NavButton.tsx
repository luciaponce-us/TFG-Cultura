import { Button } from "@chakra-ui/react";
import { Link } from "react-router-dom";
import type { To } from "react-router-dom";

type Props = {
  to: To;
  children: React.ReactNode;
};

export const NavButton = ({ to, children }: Props) => {
  return (
    <Button
      asChild
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
    >
      <Link to={to}>{children}</Link>
    </Button>
  );
};
