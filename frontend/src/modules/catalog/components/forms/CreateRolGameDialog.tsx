import { useState } from "react";

import { Separator } from "@chakra-ui/react";

import {
  CustomInput,
  CustomSelect,
  FormDialog,
  TextSecondary,
} from "@/modules/core/components";
import {
  handleChange,
  handleSelectChange,
  PLACEHOLDER,
} from "@/modules/core/utils/utils";

import {
  useCreateRolGame,
  useRolGame,
  useRolGameForm,
  useRolSaga,
  useUpdateRolGame,
} from "../../hooks";
import {
  INITIAL_ROL_GAME,
  INITIAL_ROL_GAME_ERRORS,
  ROL_BOOK_TYPES_OPTIONS,
  type RolGameErrors,
} from "../../types/rolgame";
import type { CreateItemDialogProps } from "../../types";
import {
  MAX_LENGTH,
  validateRolGameForm,
} from "../../validations/rolgame.validations";
import { AdminItemInfoForm, ItemImageInput } from "..";

interface CreateRolGameDialogProps extends CreateItemDialogProps {
  readonly sagaId: string;
}

export function CreateRolGameDialog({
  isOpen,
  setIsOpen,
  sagaId,
  itemId,
}: CreateRolGameDialogProps) {
  const { data: rolGameToEdit, isLoading: isRolGameToEditLoading } =
    useRolGame(itemId);
  const { data: rolSaga, isLoading: isRolSagaLoading } = useRolSaga(sagaId);
  const { form, setForm } = useRolGameForm(
    itemId,
    sagaId,
    rolGameToEdit,
    isRolGameToEditLoading || isRolSagaLoading,
  );
  const [errors, setErrors] = useState<RolGameErrors>(INITIAL_ROL_GAME_ERRORS);
  const [image, setImage] = useState<File | null>(null);
  function resetForm() {
    setForm(INITIAL_ROL_GAME);
    setErrors(INITIAL_ROL_GAME_ERRORS);
    setImage(null);
  }
  const { mutateAsync: createRolGame, isPending: submitting } =
    useCreateRolGame(form, image, setErrors, setIsOpen, resetForm);
  const { mutateAsync: updateRolGame, isPending: updating } = useUpdateRolGame(
    itemId,
    form,
    image,
    setErrors,
    setIsOpen,
    resetForm,
  );
  const loading =
    submitting || updating || isRolGameToEditLoading || isRolSagaLoading;

  const handleTypeChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "type", form, setErrors, setForm);

  async function handleSubmit() {
    const isValid = validateRolGameForm(form, setErrors);

    if (!isValid) return;

    if (itemId) {
      await updateRolGame();
    } else {
      await createRolGame();
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
      resetForm={resetForm}
    >
      <ItemImageInput
        image={image}
        setImage={setImage}
        loading={isRolGameToEditLoading}
        placeholder={PLACEHOLDER.ROLGAME}
        disabled={loading}
        imageUrl={rolGameToEdit?.imageUrl}
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
        value={form.name}
      />

      <CustomSelect
        label="Saga"
        options={rolSaga ? [{ value: rolSaga.id, label: rolSaga.name }] : []}
        placeholder="Cargando..."
        defaultValue={[form.sagaId]}
        value={[form.sagaId]}
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
        value={[form.type]}
        error={errors.type ?? ""}
        disabled={loading}
        required
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
        value={form.description}
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
