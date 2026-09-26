import type { Dispatch, SetStateAction } from "react";

export interface CreateItemDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: (isOpen: boolean) => void;
  readonly itemId?: string;
  readonly sectionDefaultValue?: string;
  readonly sagaId?: string;
}

export interface CreateRolSagaDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: Dispatch<SetStateAction<boolean>>;
  readonly rolSagaId?: string;
}
