import { useState, type Dispatch, type SetStateAction } from "react";
import {
  INITIAL_BOOK,
  type Book,
  type BookRequest,
} from "../../types/book";
import { toBookRequest } from "../../utils/item.utils";

export function useBookForm(
  bookId: string | undefined,
  bookToUpdate?: Book,
): { form: BookRequest; setForm: Dispatch<SetStateAction<BookRequest>> } {
  const [formOverride, setFormOverride] = useState<{
    bookId: string | undefined;
    value: BookRequest;
  }>();

  const loadedForm = bookToUpdate
    ? toBookRequest(bookToUpdate)
    : INITIAL_BOOK;

  const form =
    formOverride?.bookId === bookId && formOverride !== undefined
      ? formOverride.value
      : loadedForm;

  const setForm: Dispatch<SetStateAction<BookRequest>> = (nextForm) => {
    setFormOverride((currentOverride) => {
      const currentForm =
        currentOverride?.bookId === bookId &&
        currentOverride !== undefined
          ? currentOverride.value
          : loadedForm;

      return {
        bookId,
        value:
          typeof nextForm === "function" ? nextForm(currentForm) : nextForm,
      };
    });
  };

  return { form, setForm };
}
