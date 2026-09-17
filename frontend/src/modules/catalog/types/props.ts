export interface CreateItemDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: (isOpen: boolean) => void;
  readonly itemId?: string;
  readonly sectionDefaultValue?: string;
}