import { HStack, Text } from "@chakra-ui/react";
import { useBook, useBooksBySaga, useSaga } from "../hooks";
import { CreateBookDialog, MoreItemsFromSaga } from "../components";
import { useParams } from "react-router-dom";
import { ITEM_TYPES } from "../types";
import type { Saga } from "../types/saga";
import type { Book } from "../types/book";
import { parseBookType } from "../utils/books.utils";
import { ItemPage } from "./ItemPage";
import { PLACEHOLDER } from "@/modules/core/utils/utils";

export function BookPage() {
  const { bookId } = useParams<{ bookId: string }>();
  const { data: book, isLoading, isError } = useBook(bookId);
  const { data: saga } = useSaga(book?.saga ?? "");

  return (
    <ItemPage
      item={book}
      isLoading={isLoading}
      isError={isError}
      itemId={bookId ?? ""}
      itemType={ITEM_TYPES.BOOK}
      placeholderImage={PLACEHOLDER.BOOK}
      errorMessage="Ha ocurrido un error al cargar el libro. Vuelve a intentarlo más tarde."
      CreateItemDialogComponent={CreateBookDialog}
      subtitle={Subtitle({ book })}
      extraInfo={ExtraInfo({ book })}
      sagaComponent={<SagaBooks saga={saga} selfId={book?.id} />}
      sagaId={book?.saga}
    />
  );
}

function Subtitle({ book }: { book: Book | undefined }): string | undefined {
  if (!book) return undefined;
  return `${parseBookType(book.type)} de ${book.author}`;
}

function ExtraInfo({
  book,
}: {
  book: Book | undefined;
}): React.ReactNode | undefined {
  if (!book || !book.isbn) return undefined;
  return (
    <HStack>
      <Text fontWeight="bold">ISBN:</Text>
      <Text>{book.isbn}</Text>
    </HStack>
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

  if (!saga) return undefined;

  return (
    <MoreItemsFromSaga
      sagaName={saga.name}
      sagaItems={sagaBooks}
      isLoading={sagaBooksLoading}
      isError={sagaBooksError}
      CreateItemDialog={CreateBookDialog}
      itemType={ITEM_TYPES.BOOK}
    />
  );
}
