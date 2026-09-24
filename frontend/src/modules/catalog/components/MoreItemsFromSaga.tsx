import { TextSecondary } from "@/modules/core/components";
import { Grid, Heading } from "@chakra-ui/react";
import type { CreateItemDialogProps, Item, ItemType } from "../types";
import { ItemCard } from "./ItemCard";

export function MoreItemsFromSaga<T extends Item>({
  sagaName,
  sagaItems,
  isLoading,
  isError,
  CreateItemDialog,
  itemType,
}: {
  sagaName: string;
  sagaItems: T[] | undefined;
  isLoading: boolean;
  isError: boolean;
  CreateItemDialog: React.ComponentType<CreateItemDialogProps>;
  itemType: ItemType;
}) {
  let content;

  if (isLoading) {
    content = <TextSecondary>Cargando libros de la saga...</TextSecondary>;
  } else if (isError) {
    content = (
      <TextSecondary>
        Ha ocurrido un error al cargar los libros de la saga. Vuelve a
        intentarlo más tarde.
      </TextSecondary>
    );
  } else if (sagaItems && sagaItems.length == 0) {
    content = <TextSecondary>No hay más libros de esta saga.</TextSecondary>;
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
        Más libros de la saga "{sagaName}"
      </Heading>
      {content}
    </>
  );
}
