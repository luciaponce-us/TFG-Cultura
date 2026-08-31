import { Button } from "@chakra-ui/react";
import { Link } from "react-router-dom";
import type { To } from "react-router-dom";

interface NavButtonProps extends React.ComponentProps<typeof Button> {
  to?: To;
  children: React.ReactNode;
  onClick?: () => void;
}

export const NavButton = ({
  to,
  children,
  onClick,
  ...props
}: NavButtonProps) => {
  if (to) {
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
        onClick={onClick}
        {...props}
      >
        <Link to={to}>{children}</Link>
      </Button>
    );
  }

  return (
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
      onClick={onClick}
      {...props}
    >
      {children}
    </Button>
  );
};
