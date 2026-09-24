import { TextSecondary } from "@/modules/core/components";
import { Grid, Heading } from "@chakra-ui/react";
import type { CreateItemDialogProps, Item, ItemType } from "../types";
import { ItemCard } from "./ItemCard";

interface MoreItemsFromSagaProps<T extends Item> {
  sagaItems: T[] | undefined;
  isLoading: boolean;
  isError: boolean;
  CreateItemDialog: React.ComponentType<CreateItemDialogProps>;
  itemType: ItemType;
  title: string;
  loadingText: string;
  errorText: string;
  emptyText: string;
}

export function MoreItemsFromSaga<T extends Item>({
  sagaItems,
  isLoading,
  isError,
  CreateItemDialog,
  itemType,
  title,
  loadingText,
  errorText,
  emptyText,
}: MoreItemsFromSagaProps<T>) {
  let content;

  if (isLoading) {
    content = <TextSecondary>{loadingText}</TextSecondary>;
  } else if (isError) {
    content = (
      <TextSecondary>
        {errorText}
      </TextSecondary>
    );
  } else if (sagaItems && sagaItems.length == 0) {
    content = <TextSecondary>{emptyText}</TextSecondary>;
  } else if (sagaItems && sagaItems.length > 0) {
    content = (
      <Grid
        templateColumns={{
          base: "1fr 1fr",
          md: "1fr 1fr 1fr",
          lg: "1fr 1fr 1fr 1fr 1fr",
        }}
        gap={2}
        w="100%"
        h="300px"
      >
        {sagaItems.map((item) => (
          <ItemCard
            key={item.id}
            item={item}
            CreateItemDialog={CreateItemDialog}
            type={itemType}
            isSagaItem={true}
          />
        ))}
      </Grid>
    );
  }

  return (
    <>
      <Heading as="h2" size="md" mt={6} mb={4}>
        {title}
      </Heading>
      {content}
    </>
  );
}
