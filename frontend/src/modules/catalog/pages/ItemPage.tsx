import {
  Flex,
  Grid,
  Heading,
  Image,
  VStack,
  Text,
  Box,
  Spinner,
} from "@chakra-ui/react";
import { CustomButton, toaster } from "@/modules/core/components";
import { useState } from "react";
import { AdminItemInfoSideBar, ItemDescription } from "../components";
import { useAuth } from "@/modules/core/context/useAuth";
import { CategoryTag } from "@/modules/categories/components/CategoryTag";
import type { CreateItemDialogProps, Item, ItemType } from "../types";

interface ItemPageProps<T extends Item> {
  item: T | undefined;
  isLoading: boolean;
  isError: boolean;
  itemId: string;
  itemType: ItemType;
  placeholderImage: string;
  errorMessage: string;
  CreateItemDialogComponent: React.ComponentType<CreateItemDialogProps>;
  subtitle?: string | React.ReactNode;
  extraInfo?: React.ReactNode;
  sagaComponent?: React.ReactNode;
  sagaId?: string;
}

export function ItemPage<T extends Item>({
  item,
  isLoading,
  isError,
  itemId,
  itemType,
  placeholderImage,
  errorMessage,
  CreateItemDialogComponent,
  subtitle,
  extraInfo,
  sagaComponent,
  sagaId,
}: ItemPageProps<T>) {
  const { isAdmin } = useAuth();
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);

  let content;

  if (isLoading) {
    content = (
      <Box flex={1} display="flex" alignItems="center" justifyContent="center">
        <Spinner size="xl" color="principal.500" />
      </Box>
    );
  } else if (isError) {
    content = (
      <VStack
        flex={1}
        display="flex"
        alignItems="center"
        justifyContent="center"
      >
        <Image
          src="/images/character_sad.png"
          alt="Error"
          width="300px"
          height="300px"
          mb={4}
        />
        <Text color="gray.700" fontSize="lg" textAlign="center">
          {errorMessage}
        </Text>
      </VStack>
    );
  } else if (item) {
    content = (
      <VStack w="100%" minH={0} minW={0}>
        <Grid
          w="100%"
          minH={0}
          alignItems={{ base: "stretch", md: "start" }}
          templateColumns={{ base: "1fr", md: "250px minmax(0, 1fr)" }}
          gap={6}
          h="fit-content"
          justifyContent="top"
        >
          <Image
            src={item.imageUrl ?? placeholderImage}
            alt={item.name}
            width="auto"
            maxH={{ base: "50vh", md: "500px" }}
            minH={0}
            borderRadius="md"
            aspectRatio="2/3"
            justifySelf="center"
          />

          <VStack
            w="100%"
            justify="space-between"
            h="100%"
            minW={0}
            py={4}
            gap={6}
          >
            <VStack w="100%" align="start" gap={2}>
              <Heading as="h1" wordBreak="break-word">
                {item.name}
              </Heading>
              {subtitle && (
                <Text
                  fontSize="sm"
                  color="gray.500"
                  wordBreak="break-all"
                  lineClamp={1}
                >
                  {subtitle}
                </Text>
              )}
              <ItemDescription description={item.description} />
              <Box
                display="flex"
                flexWrap="wrap"
                gap={1}
                w="100%"
                justifyContent="start"
              >
                {item.categories.length > 0 &&
                  item.categories.map((category) => (
                    <CategoryTag key={category.id} category={category} />
                  ))}
              </Box>
              {extraInfo}
            </VStack>
            <Box alignSelf="center">
              <CustomButton
                onClick={() =>
                  toaster.create({
                    title: "Funcionalidad en desarrollo",
                    description: "Esta funcionalidad aún no está disponible.",
                  })
                }
                w="fit-content"
                disabled={!item.loanAvailable}
              >
                Solicitar préstamo
              </CustomButton>
            </Box>
          </VStack>
        </Grid>
        {sagaComponent}
      </VStack>
    );
  }

  return (
    <>
      <Grid
        templateColumns={{ base: "1fr", md: isAdmin ? "1fr 2.5fr" : "1fr" }}
        gap={10}
        maxW={isAdmin ? "70vw" : "50vw"}
      >
        {isAdmin && (
          <AdminItemInfoSideBar
            item={item}
            isLoading={isLoading}
            type={itemType}
            CreateItemDialog={CreateItemDialogComponent}
            sagaId={sagaId}
          />
        )}

        <Flex
          bg="background"
          borderRadius="xl"
          boxShadow="lg"
          p={6}
          align="stretch"
          justify="flex-start"
          w="100%"
          minW={{ base: "100%", md: "800px" }}
          h="fit-content"
          gap={6}
        >
          {content}
        </Flex>
      </Grid>
      {isEditDialogOpen && (
        <CreateItemDialogComponent
          isOpen={isEditDialogOpen}
          setIsOpen={setIsEditDialogOpen}
          itemId={itemId}
          sagaId={sagaId}
        />
      )}
    </>
  );
}
