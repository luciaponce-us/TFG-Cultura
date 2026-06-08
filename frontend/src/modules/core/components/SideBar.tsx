import { Flex } from "@chakra-ui/react";

interface SideBarProps extends React.ComponentProps<typeof Flex> {
  hideOnMobile?: boolean;
}

export const SideBar = ({
  children,
  hideOnMobile = true,
  ...props
}: SideBarProps) => {
  return (
    <Flex
      bg="background"
      borderRadius="xl"
      boxShadow="md"
      p={6}
      direction="column"
      align="center"
      justify="flex-start"
      height="fit-content"
      gap={6}
      {...(hideOnMobile ? { hideBelow: "md" } : {})}
      {...props}
    >
      {children}
    </Flex>
  );
};
