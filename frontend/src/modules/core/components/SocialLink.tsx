import { Link, Text } from "@chakra-ui/react";

type Props = {
  label: string;
  href: string;
  icon: React.ComponentType<{ size?: number }>;
  color?: string;
  fontSize?: string;
};

export const SocialLink = ({
  label,
  href,
  icon: Icon,
  color = "principal",
  fontSize = "md",
}: Props) => {
  const getColor = (color: string): { default: string; hover: string } => {
    switch (color) {
      case "principal":
        return { default: "principal.700", hover: "principal.500" };
      case "white":
        return { default: "white", hover: "gray.300" };
      default:
        return { default: color, hover: color };
    }
  };

  const getIconSize = (fontSize: string): number => {
    switch (fontSize) {
      case "sm":
        return 25;
      case "md":
        return 30;
      case "lg":
        return 40;
      default:
        return 30;
    }
  };

  const getGapSize = (fontSize: string): number => {
    switch (fontSize) {
      case "sm":
        return 2;
      case "md":
        return 3;
      case "lg":
        return 4;
      default:
        return 3;
    }
  };

  return (
    <Link
      key={label}
      href={href}
      target="_blank"
      rel="noopener noreferrer"
      display="flex"
      alignItems="center"
      gap={getGapSize(fontSize)}
      color={getColor(color).default}
      _hover={{ color: getColor(color).hover }}
    >
      <Icon size={getIconSize(fontSize)} />
      <Text fontSize={fontSize}>{label}</Text>
    </Link>
  );
};
