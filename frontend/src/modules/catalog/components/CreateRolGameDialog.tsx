import { useState } from "react";
import {
  useCreateRolGame,
  useRolGame,
  useRolGameForm,
  useRolSaga,
  useUpdateRolGame,
} from "../hooks";
import {
  handleChange,
  handleSelectChange,
  PLACEHOLDER,
} from "@/modules/core/utils/utils";
import { Separator } from "@chakra-ui/react";
import {
  CustomInput,
  CustomSelect,
  FormDialog,
  TextSecondary,
} from "@/modules/core/components";
import {
  INITIAL_ROL_GAME_ERRORS,
  ROL_BOOK_TYPES_OPTIONS,
  type RolGameErrors,
} from "../types/rolgame";
import {
  MAX_LENGTH,
  validateRolGameForm,
} from "../validations/rolgame.validations";
import { AdminItemInfoForm } from "./AdminItemInfoForm";
import type { CreateItemDialogProps } from "../types/props";
import { ItemImageInput } from "./ItemImageInput";

interface CreateRolGameDialogProps extends CreateItemDialogProps {
  readonly sagaId: string;
}

export function CreateRolGameDialog({
  isOpen,
  setIsOpen,
  sagaId,
  itemId,
}: CreateRolGameDialogProps) {
  const { data: rolGameToEdit } = useRolGame(itemId);
  const { form, setForm } = useRolGameForm(itemId, sagaId, rolGameToEdit);
  const [errors, setErrors] = useState<RolGameErrors>(INITIAL_ROL_GAME_ERRORS);
  const [image, setImage] = useState<File | null>(null);
  const {
    mutateAsync: createRolGame,
    isPending: submitting,
    isError: isCreateRolGameError,
  } = useCreateRolGame(form, image, setErrors, setIsOpen);
  const {
    mutateAsync: updateRolGame,
    isPending: updating,
    isError: isUpdateRolGameError,
  } = useUpdateRolGame(itemId, form, image, setErrors, setIsOpen);
  const loading = submitting || updating;

  const { data: rolSaga } = useRolSaga(sagaId);

  const handleTypeChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "type", form, setErrors, setForm);

  async function handleSubmit() {
    const errors = validateRolGameForm(form);
    console.error("Validation errors:", errors);
    setErrors(errors);
    if (Object.values(errors).some(Boolean)) {
      return;
    }
    if (itemId) {
      await updateRolGame();
    } else {
      await createRolGame();
    }
    if (!isCreateRolGameError && !isUpdateRolGameError) {
      setIsOpen(false);
    }
  }

  return (
    <FormDialog
      isOpen={isOpen}
      setIsOpen={setIsOpen}
      title={
        rolGameToEdit ? `Editando ${rolGameToEdit.name}` : "Crear juego de rol"
      }
      handleSubmit={async () => await handleSubmit()}
      submitButtonText={itemId ? "Actualizar" : "Crear"}
    >
      <ItemImageInput
        image={image}
        setImage={setImage}
        loading={loading}
        placeholder={PLACEHOLDER.ROLGAME}
      />

      <CustomInput
        label="Nombre del libro"
        name="name"
        placeholder="Introduce el nombre del libro del juego de rol..."
        required
        error={errors.name ?? ""}
        onChange={(e) => handleChange(e, form, setErrors, setForm)}
        maxLength={MAX_LENGTH.NAME}
        disabled={loading}
        defaultValue={form.name}
      />

      <CustomSelect
        label="Saga"
        options={rolSaga ? [{ value: rolSaga.id, label: rolSaga.name }] : []}
        placeholder="Cargando..."
        defaultValue={[form.sagaId]}
        disabled
        required
      />
      <TextSecondary>
        La saga se asignará automáticamente según la página en la que te
        encuentres.
      </TextSecondary>

      <CustomSelect
        label="Tipo de libro del juego de rol"
        name="type"
        options={ROL_BOOK_TYPES_OPTIONS}
        onValueChange={handleTypeChange}
        placeholder="Selecciona el tipo de libro de juego de rol"
        defaultValue={[form.type]}
        disabled={loading}
      />

      <CustomInput
        label="Descripción"
        name="description"
        placeholder="Introduce la descripción del libro del juego de rol..."
        error={errors.description ?? ""}
        onChange={(e) => handleChange(e, form, setErrors, setForm)}
        textarea
        maxInputHeight="240px"
        maxLength={MAX_LENGTH.DESCRIPTION}
        disabled={loading}
        defaultValue={form.description}
      />

      <Separator />
      <AdminItemInfoForm
        form={form}
        setForm={setForm}
        errors={errors}
        setErrors={setErrors}
        loading={loading}
      />
    </FormDialog>
  );
}
