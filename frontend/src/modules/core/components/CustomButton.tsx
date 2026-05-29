import { Button, type ButtonProps } from "@chakra-ui/react";
import type { ReactNode } from "react";

interface CustomButtonProps extends ButtonProps {
  children: ReactNode;
  onClick: () => void;
  color?: string;
  isPagination?: boolean;
}

export const CustomButton = ({
  children,
  onClick,
  color = "principal",
  isPagination = false,
  ...props
}: CustomButtonProps) => {
  if (color === "transparent") {
    return (
      <Button
        bg="transparent"
        color="principal.800"
        borderRadius="full"
        transition="all 0.2s"
        onClick={onClick}
        _hover={{
          bg: "transparente.20",
        }}
        _active={{
          bg: "transparente.80",
          transform: "scale(0.98)",
        }}
        {...props}
      >
        {children}
      </Button>
    );
  }

  return (
    <Button
      bg={`${color}.500`}
      color="white"
      borderRadius="full"
      transition="all 0.2s"
      onClick={onClick}
      _hover={
        isPagination
          ? undefined
          : {
              bg: `${color}.600`,
            }
      }
      _active={
        isPagination
          ? undefined
          : {
              bg: `${color}.700`,
              transform: "scale(0.98)",
            }
      }
      _disabled={
        isPagination
          ? {
              bg: `${color}.500`,
              opacity: 1,
              cursor: "not-allowed",
            }
          : undefined
      }
      {...props}
    >
      {children}
    </Button>
  );
};
