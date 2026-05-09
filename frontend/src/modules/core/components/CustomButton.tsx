import { Button, type ButtonProps } from "@chakra-ui/react";
import type { ReactNode } from "react";

interface CustomButtonProps extends ButtonProps {
  children: ReactNode;
  onClick: () => void;
  color?: string;
}

export const CustomButton = ({
  children,
  onClick,
  color = "principal",
  ...props
}: CustomButtonProps) => {
  return (
    <Button
      bg={`${color}.500`}
      color="white"
      borderRadius="full"
      transition="all 0.2s"
      onClick={onClick}
      _hover={{
        bg: `${color}.600`,
      }}
      _active={{
        bg: `${color}.700`,
        transform: "scale(0.98)",
      }}
      {...props}
    >
      {children}
    </Button>
  );
};
