import { useState } from "react";

import { Separator } from "@chakra-ui/react";

import {
  CustomDateInput,
  CustomInput,
  CustomSelect,
  FormDialog,
} from "@/modules/core/components";
import { CategoriesSelect, CreateCategoryDialog } from "@/modules/categories/components";
import { SectionSelect } from "@/modules/sections/components";
import { handleChange, handleSelectChange, PLACEHOLDER } from "@/modules/core/utils/utils";

import { useCreateVideoGame } from "../hooks";
import {
  INITIAL_VIDEO_GAME,
  INITIAL_VIDEO_GAME_ERRORS,
  PLATFORM_OPTIONS,
  type VideoGameErrors,
  type VideoGameRequest,
} from "../types/videogame";
import type { CreateItemDialogProps } from "../types";
import { MAX_LENGTH, validateVideoGameForm } from "../validations/videogame.validations";
import { AdminItemInfoForm, ItemImageInput } from "./";

export function CreateVideoGameDialog({
  isOpen,
  setIsOpen,
}: CreateItemDialogProps) {
  const [form, setForm] = useState<VideoGameRequest>(INITIAL_VIDEO_GAME);
  const [errors, setErrors] = useState<VideoGameErrors>(
    INITIAL_VIDEO_GAME_ERRORS,
  );
  const [image, setImage] = useState<File | null>(null);
  const { mutateAsync: createVideoGame, isPending: submitting } =
    useCreateVideoGame(form, image, setErrors, setIsOpen);
  const [categoryDialogOpen, setCategoryDialogOpen] = useState(false);

  const handlePlatformChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "platform", form, setErrors, setForm);

  async function handleSubmit() {
    validateVideoGameForm(form, setErrors);
    if (errors != INITIAL_VIDEO_GAME_ERRORS) return;
    await createVideoGame();
  }

  return (
    <>
      <FormDialog
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        title="Crear videojuego"
        handleSubmit={handleSubmit}
        submitButtonText="Crear"
        resetForm={() => {
          setForm(INITIAL_VIDEO_GAME);
          setErrors(INITIAL_VIDEO_GAME_ERRORS);
          setImage(null);
        }}
      >
        <ItemImageInput
          image={image}
          setImage={setImage}
          loading={submitting}
          placeholder={PLACEHOLDER.VIDEOGAME}
        />

        <CustomInput
          label="Título"
          name="name"
          placeholder="Introduce el título..."
          required
          error={errors.name ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.NAME}
        />
        <CustomSelect
          label="Plataforma"
          name="platform"
          options={PLATFORM_OPTIONS}
          onValueChange={handlePlatformChange}
          placeholder="Selecciona la plataforma"
          value={[form.platform]}
          error={errors.platform ?? ""}
          required
        />
        <CategoriesSelect
          form={form}
          setForm={setForm}
          onCreateCategory={() => setCategoryDialogOpen(true)}
          error={errors.categoriesIds}
        />
        <CustomInput
          label="Sinopsis"
          name="description"
          placeholder="Proporciona una sinopsis o descripción del videojuego"
          error={errors.description ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          textarea
          maxInputHeight="240px"
          maxLength={MAX_LENGTH.DESCRIPTION}
        />
        <CustomDateInput
          label="Fecha de estreno"
          value={form.releaseDate ?? ""}
          error={errors.releaseDate ?? ""}
          onChange={(value) =>
            setForm((prev) => ({ ...prev, releaseDate: value }))
          }
        />
        <CustomInput
          label="Tráiler"
          name="trailerUrl"
          placeholder="https://www.youtube.com/embed/..."
          error={errors.trailerUrl ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.TRAILER_URL}
        />
        <SectionSelect
          form={form}
          setForm={setForm}
          errors={errors}
          setErrors={setErrors}
          defaultValueText="Informática"
        />

        <Separator />
        <AdminItemInfoForm
          form={form}
          setForm={setForm}
          errors={errors}
          setErrors={setErrors}
          loading={submitting}
          loanAvailable={false}
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
