import {
  Flex,
  Grid,
  Heading,
  HStack,
  Image,
  VStack,
  Text,
  Box,
  Spinner,
  Link,
} from "@chakra-ui/react";
import {
  CustomButton,
  TextSecondary,
  toaster,
} from "@/modules/core/components";
import { useEffect, useRef, useState } from "react";
import { useBook, useBooksBySaga, useSaga } from "../hooks";
import {
  AdminItemInfoSideBar,
  CreateBookDialog,
  ItemCard,
} from "../components";
import { useParams } from "react-router-dom";
import { PLACEHOLDER } from "@/modules/core/utils/utils";
import { useAuth } from "@/modules/core/context/useAuth";
import { parseBookType } from "../utils/books.utils";
import { CategoryTag } from "@/modules/categories/components/CategoryTag";
import { ITEM_TYPES } from "../types";
import type { Saga } from "../types/saga";

export function BookPage() {
  const { isAdmin } = useAuth();
  const { bookId } = useParams<{ bookId: string }>();
  const { data: book, isLoading, isError } = useBook(bookId);
  const { data: saga } = useSaga(book?.saga ?? "");
  const [isEditDialogOpen, setIsEditDialogOpen] = useState(false);
  const [descriptionExpanded, setDescriptionExpanded] = useState(false);
  const descriptionRef = useRef<HTMLParagraphElement>(null);
  const [isDescriptionOverflowing, setIsDescriptionOverflowing] =
    useState(false);

  useEffect(() => {
    const element = descriptionRef.current;

    if (!element) return;

    setIsDescriptionOverflowing(element.scrollHeight > element.clientHeight);
  }, [book?.description, descriptionExpanded]);

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
          Ha ocurrido un error al cargar el libro. Vuelve a intentarlo más
          tarde.
        </Text>
      </VStack>
    );
  } else if (book) {
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
            src={book.imageUrl ?? PLACEHOLDER.BOOK}
            alt={book.name}
            width="auto"
            maxH={{ base: "50vh", md: "500px" }}
            minH={0}
            borderRadius="sm"
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
                {book.name}
              </Heading>
              <Text
                fontSize="sm"
                color="gray.500"
                wordBreak="break-all"
                lineClamp={1}
              >{`${parseBookType(book.type)} de ${book.author}`}</Text>
              {book.description && (
                <>
                  <Text
                    ref={descriptionRef}
                    wordBreak="break-word"
                    overflowWrap="break-word"
                    lang="es"
                    hyphens="auto"
                    lineClamp={descriptionExpanded ? undefined : 5}
                  >
                    {book.description}
                  </Text>

                  {(descriptionExpanded || isDescriptionOverflowing) && (
                    <Link
                      onClick={() =>
                        setDescriptionExpanded(!descriptionExpanded)
                      }
                    >
                      {descriptionExpanded ? "Ver menos" : "Ver más"}
                    </Link>
                  )}
                </>
              )}
              <Box
                display="flex"
                flexWrap="wrap"
                gap={1}
                w="100%"
                justifyContent="start"
              >
                {book.categories.length > 0 &&
                  book.categories.map((category) => (
                    <CategoryTag key={category.id} category={category} />
                  ))}
              </Box>
              {book.isbn && (
                <HStack>
                  <Text fontWeight="bold">ISBN:</Text>
                  <Text>{book.isbn}</Text>
                </HStack>
              )}
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
                disabled={!book.loanAvailable}
              >
                Solicitar préstamo
              </CustomButton>
            </Box>
          </VStack>
        </Grid>
        <SagaBooks saga={saga} selfId={book?.id} />
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
            item={book}
            isLoading={isLoading}
            type={ITEM_TYPES.BOOK}
            CreateItemDialog={CreateBookDialog}
            sagaId={saga?.id}
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
        <CreateBookDialog
          isOpen={isEditDialogOpen}
          setIsOpen={setIsEditDialogOpen}
          itemId={bookId}
        />
      )}
    </>
  );
}

function SagaBooks({
  saga,
  selfId,
}: {
  saga: Saga | undefined;
  selfId?: string;
}) {
  const {
    data: sagaBooks,
    isLoading: sagaBooksLoading,
    isError: sagaBooksError,
  } = useBooksBySaga(saga?.id ?? "", selfId);

  if (!saga) return null;

  let content;

  if (sagaBooksLoading) {
    content = <TextSecondary>Cargando libros de la saga...</TextSecondary>;
  } else if (sagaBooksError) {
    content = (
      <TextSecondary>
        Ha ocurrido un error al cargar los libros de la saga. Vuelve a
        intentarlo más tarde.
      </TextSecondary>
    );
  } else if (sagaBooks && sagaBooks.length == 0) {
    content = <TextSecondary>No hay más libros de esta saga.</TextSecondary>;
  } else if (sagaBooks && sagaBooks.length > 0) {
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
        {sagaBooks.map((book) => (
          <ItemCard
            key={book.id}
            item={book}
            CreateItemDialog={CreateBookDialog}
            type={ITEM_TYPES.BOOK}
            isSagaItem={true}
          />
        ))}
      </Grid>
    );
  }

  return (
    <>
      <Heading as="h2" size="md" mt={6} mb={4}>
        Más libros de la saga "{saga.name}"
      </Heading>
      {content}
    </>
  );
}
