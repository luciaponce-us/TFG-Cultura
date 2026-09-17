import { useState } from "react";

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

import { useCreateRolSaga } from "../hooks";
import {
  GAME_MASTERS_OPTIONS,
  INITIAL_ROL_SAGA,
  INITIAL_ROL_SAGA_ERRORS,
  type RolSagaErrors,
  type RolSagaRequest,
} from "../types/rolgame";
import {
  MAX_LENGTH,
  validateRolSagaForm,
} from "../validations/rolsaga.validations";
import { ItemImageInput } from "./";

interface CreateRolSagaDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: (isOpen: boolean) => void;
}

export function CreateRolSagaDialog({
  isOpen,
  setIsOpen,
}: CreateRolSagaDialogProps) {
  const [form, setForm] = useState<RolSagaRequest>(INITIAL_ROL_SAGA);
  const [image, setImage] = useState<File | null>(null);
  const [errors, setErrors] = useState<RolSagaErrors>(INITIAL_ROL_SAGA_ERRORS);
  const { mutateAsync: createRolSaga, isPending: submitting } =
    useCreateRolSaga(
      form,
      image,
      (errors) => setErrors(errors),
      (isOpen) => setIsOpen(isOpen),
    );

  const [categoryDialogOpen, setCategoryDialogOpen] = useState(false);

  function handleGameMasterChange({ value }: { value: string[] }) {
    handleSelectChange(value, "gameMaster", form, setErrors, setForm);
  }

  async function handleSubmit() {
    validateRolSagaForm(form, setErrors);
    if (errors != INITIAL_ROL_SAGA_ERRORS) return;
    await createRolSaga();
  }

  return (
    <>
      <FormDialog
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        title="Crear saga de juegos de rol"
        handleSubmit={handleSubmit}
        submitButtonText="Crear"
        resetForm={() => {
          setForm(INITIAL_ROL_SAGA);
          setErrors(INITIAL_ROL_SAGA_ERRORS);
          setImage(null);
        }}
      >
        <ItemImageInput
          image={image}
          setImage={setImage}
          loading={submitting}
          placeholder={PLACEHOLDER.ROLSAGA}
        />

        <CustomInput
          label="Nombre"
          name="name"
          placeholder="Introduce el nombre de la saga..."
          required
          error={errors.name ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.NAME}
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
        />

        <CategoriesSelect
          form={form}
          setForm={setForm}
          onCreateCategory={() => setCategoryDialogOpen(true)}
          error={errors.categoriesIds}
        />

        <CustomInput
          label="Sitio web"
          name="website"
          placeholder="Proporciona una url al sitio web de la saga..."
          error={errors.website ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.WEBSITE}
        />

        <CustomInput
          label="Hoja de personaje"
          name="characterSheetUrl"
          placeholder="Proporciona una url a la hoja de personaje de la saga..."
          error={errors.characterSheetUrl ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.CHARACTER_SHEET_URL}
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
        />

        <CustomInput
          label="Dados"
          name="dice"
          placeholder="d4, d6, d8, d10, d12, d20, d100"
          error={errors.dice ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.DICE}
        />

        <CustomInput
          label="Jugadores recomendados"
          name="recommendedPlayers"
          placeholder="3-5 jugadores"
          error={errors.recommendedPlayers ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.RECOMMENDED_PLAYERS}
        />

        <SectionSelect
          form={form}
          setForm={setForm}
          errors={errors}
          setErrors={setErrors}
          defaultValueText="Rol"
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
