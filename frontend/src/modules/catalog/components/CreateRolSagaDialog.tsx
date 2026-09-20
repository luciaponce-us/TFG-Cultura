import { useState, type Dispatch, type SetStateAction } from "react";

import {
  CustomInput,
  CustomSelect,
  FormDialog,
} from "@/modules/core/components";
import {
  handleChange,
  handleSelectChange,
  PLACEHOLDER,
} from "@/modules/core/utils/utils";
import {
  CategoriesSelect,
  CreateCategoryDialog,
} from "@/modules/categories/components";
import { SectionSelect } from "@/modules/sections/components";

import {
  useCreateRolSaga,
  useRolSaga,
  useRolSagaForm,
  useUpdateRolSaga,
} from "../hooks";
import {
  GAME_MASTERS_OPTIONS,
  INITIAL_ROL_SAGA,
  INITIAL_ROL_SAGA_ERRORS,
  type RolSagaErrors,
} from "../types/rolgame";
import {
  MAX_LENGTH,
  validateRolSagaForm,
} from "../validations/rolsaga.validations";
import { ItemImageInput } from "./";

interface CreateRolSagaDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: Dispatch<SetStateAction<boolean>>;
  readonly rolSagaId?: string;
}

export function CreateRolSagaDialog({
  isOpen,
  setIsOpen,
  rolSagaId,
}: CreateRolSagaDialogProps) {
  const { data: rolSagaToUpdate, isLoading: isRolSagaToEditLoading } =
    useRolSaga(rolSagaId);
  const { form, setForm } = useRolSagaForm(
    rolSagaId,
    rolSagaToUpdate,
    isRolSagaToEditLoading,
  );
  const [image, setImage] = useState<File | null>(null);
  const [errors, setErrors] = useState<RolSagaErrors>(INITIAL_ROL_SAGA_ERRORS);
  function resetForm() {
    setForm(INITIAL_ROL_SAGA);
    setErrors(INITIAL_ROL_SAGA_ERRORS);
    setImage(null);
  }
  const { mutateAsync: createRolSaga, isPending: creating } = useCreateRolSaga(
    form,
    image,
    (errors) => setErrors(errors),
    (isOpen) => setIsOpen(isOpen),
  );
  const { mutateAsync: updateRolSaga, isPending: updating } = useUpdateRolSaga(
    rolSagaId ? rolSagaId : "",
    form,
    image,
    setErrors,
    setIsOpen,
    resetForm,
  );

  const submitting = creating || updating;

  const [categoryDialogOpen, setCategoryDialogOpen] = useState(false);

  function handleGameMasterChange({ value }: { value: string[] }) {
    handleSelectChange(value, "gameMaster", form, setErrors, setForm);
  }

  async function handleSubmit() {
    const isValid = validateRolSagaForm(
      form,
      setErrors,
      rolSagaId !== undefined,
    );
    if (!isValid) return;
    if (rolSagaId) {
      await updateRolSaga();
    } else {
      await createRolSaga();
    }
  }

  if (rolSagaId && isRolSagaToEditLoading) {
    return null; // Esperando a que se cargue la saga de juegos de rol a editar
  }

  return (
    <>
      <FormDialog
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        title={
          rolSagaToUpdate
            ? `Editando "${rolSagaToUpdate.name}"`
            : "Crear saga de juegos de rol"
        }
        handleSubmit={handleSubmit}
        submitButtonText={rolSagaToUpdate ? "Guardar" : "Crear"}
        resetForm={resetForm}
      >
        <ItemImageInput
          image={image}
          setImage={setImage}
          loading={isRolSagaToEditLoading}
          disabled={submitting}
          placeholder={rolSagaToUpdate?.imageUrl || PLACEHOLDER.ROLSAGA}
        />

        <CustomInput
          label="Nombre"
          name="name"
          placeholder="Introduce el nombre de la saga..."
          required
          error={errors.name ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.NAME}
          defaultValue={form.name}
          value={form.name}
          disabled={submitting}
        />

        <CustomInput
          label="Descripción"
          name="description"
          placeholder="Proporciona una descripción de la saga de juegos de rol..."
          error={errors.description ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          textarea
          maxInputHeight="125px"
          maxLength={MAX_LENGTH.DESCRIPTION}
          required
          defaultValue={form.description}
          value={form.description}
          disabled={submitting}
        />

        <CategoriesSelect
          form={form}
          setForm={setForm}
          onCreateCategory={() => setCategoryDialogOpen(true)}
          error={errors.categoriesIds}
          disabled={submitting}
        />

        <CustomInput
          label="Sitio web"
          name="website"
          placeholder="Proporciona una url al sitio web de la saga..."
          error={errors.website ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.WEBSITE}
          defaultValue={form.website}
          value={form.website}
          disabled={submitting}
        />

        <CustomInput
          label="Hoja de personaje"
          name="characterSheetUrl"
          placeholder="Proporciona una url a la hoja de personaje de la saga..."
          error={errors.characterSheetUrl ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.CHARACTER_SHEET_URL}
          defaultValue={form.characterSheetUrl}
          value={form.characterSheetUrl}
          disabled={submitting}
        />

        <CustomSelect
          label="Game Master"
          name="gameMaster"
          options={GAME_MASTERS_OPTIONS}
          onValueChange={handleGameMasterChange}
          placeholder="Selecciona el Game Master"
          value={[form.gameMaster]}
          error={errors.gameMaster ?? ""}
          required
          defaultValue={[form.gameMaster]}
          disabled={submitting}
        />

        <CustomInput
          label="Dados"
          name="dice"
          placeholder="d4, d6, d8, d10, d12, d20, d100"
          error={errors.dice ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.DICE}
          defaultValue={form.dice}
          value={form.dice}
          disabled={submitting}
        />

        <CustomInput
          label="Jugadores recomendados"
          name="recommendedPlayers"
          placeholder="3-5 jugadores"
          error={errors.recommendedPlayers ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.RECOMMENDED_PLAYERS}
          defaultValue={form.recommendedPlayers}
          value={form.recommendedPlayers}
          disabled={submitting}
        />

        <SectionSelect
          form={form}
          setForm={setForm}
          errors={errors}
          setErrors={setErrors}
          defaultValueText="Rol"
          disabled={submitting}
        />
      </FormDialog>

      <CreateCategoryDialog
        isOpen={categoryDialogOpen}
        setIsOpen={setCategoryDialogOpen}
        onCategoryCreated={(category) =>
          setForm((prev) => ({
            ...prev,
            categoriesIds: [...prev.categoriesIds, category.id],
          }))
        }
      />
    </>
  );
}
