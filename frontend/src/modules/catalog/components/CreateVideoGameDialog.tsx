import { useState } from "react";

import { Separator } from "@chakra-ui/react";

import {
  CustomDateInput,
  CustomInput,
  CustomSelect,
  FormDialog,
} from "@/modules/core/components";
import {
  CategoriesSelect,
  CreateCategoryDialog,
} from "@/modules/categories/components";
import { SectionSelect } from "@/modules/sections/components";
import {
  handleChange,
  handleSelectChange,
  PLACEHOLDER,
} from "@/modules/core/utils/utils";

import {
  useCreateVideoGame,
  useUpdateVideogame,
  useVideogame,
  useVideoGameForm,
} from "../hooks";
import {
  INITIAL_VIDEO_GAME,
  INITIAL_VIDEO_GAME_ERRORS,
  PLATFORM_OPTIONS,
  type VideoGameErrors,
} from "../types/videogame";
import type { CreateItemDialogProps } from "../types";
import {
  MAX_LENGTH,
  validateVideoGameForm,
} from "../validations/videogame.validations";
import { AdminItemInfoForm, ItemImageInput } from "./";

export function CreateVideoGameDialog({
  isOpen,
  setIsOpen,
  itemId,
}: CreateItemDialogProps) {
  const { data: videoGameToUpdate, isLoading: isVideoGameToEditLoading } =
    useVideogame(itemId);
  const { form, setForm } = useVideoGameForm(
    itemId,
    videoGameToUpdate,
    isVideoGameToEditLoading,
  );
  const [errors, setErrors] = useState<VideoGameErrors>(
    INITIAL_VIDEO_GAME_ERRORS,
  );
  const [image, setImage] = useState<File | null>(null);
  function resetForm() {
    setForm(INITIAL_VIDEO_GAME);
    setErrors(INITIAL_VIDEO_GAME_ERRORS);
    setImage(null);
  }
  const { mutateAsync: createVideoGame, isPending: submitting } =
    useCreateVideoGame(form, image, setErrors, setIsOpen, resetForm);
  const { mutateAsync: updateVideoGame, isPending: updating } =
    useUpdateVideogame(itemId, form, image, setErrors, setIsOpen, resetForm);
  const loading = submitting || updating;
  const [categoryDialogOpen, setCategoryDialogOpen] = useState(false);

  const handlePlatformChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "platform", form, setErrors, setForm);

  async function handleSubmit() {
    const isValid = validateVideoGameForm(form, setErrors);
    if (!isValid) return;
    if (itemId) {
      await updateVideoGame();
    } else {
      await createVideoGame();
    }
  }

  if (itemId && isVideoGameToEditLoading) {
    return null; // Esperando a que se cargue el videojuego a editar
  }

  return (
    <>
      <FormDialog
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        title={
          videoGameToUpdate
            ? `Editando "${videoGameToUpdate.name}"`
            : "Crear videojuego"
        }
        handleSubmit={handleSubmit}
        submitButtonText={itemId ? "Guardar" : "Crear"}
        resetForm={resetForm}
      >
        <ItemImageInput
          image={image}
          setImage={setImage}
          loading={isVideoGameToEditLoading}
          placeholder={PLACEHOLDER.VIDEOGAME}
          disabled={loading}
        />

        <CustomInput
          label="Título"
          name="name"
          placeholder="Introduce el título..."
          required
          error={errors.name ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.NAME}
          value={form.name}
          defaultValue={form.name}
          disabled={loading}
        />
        <CustomSelect
          label="Plataforma"
          name="platform"
          options={PLATFORM_OPTIONS}
          onValueChange={handlePlatformChange}
          placeholder="Selecciona la plataforma"
          value={[form.platform]}
          defaultValue={[form.platform]}
          error={errors.platform ?? ""}
          required
          disabled={loading}
        />
        <CategoriesSelect
          form={form}
          setForm={setForm}
          onCreateCategory={() => setCategoryDialogOpen(true)}
          error={errors.categoriesIds}
          disabled={loading}
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
          value={form.description}
          defaultValue={form.description}
          disabled={loading}
        />
        <CustomDateInput
          label="Fecha de estreno"
          value={form.releaseDate ?? ""}
          error={errors.releaseDate ?? ""}
          onChange={(value) =>
            setForm((prev) => ({ ...prev, releaseDate: value }))
          }
          disabled={loading}
        />
        <CustomInput
          label="Tráiler"
          name="trailerUrl"
          placeholder="https://www.youtube.com/embed/..."
          error={errors.trailerUrl ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.TRAILER_URL}
          value={form.trailerUrl}
          defaultValue={form.trailerUrl}
          disabled={loading}
        />
        <SectionSelect
          form={form}
          setForm={setForm}
          errors={errors}
          setErrors={setErrors}
          defaultValueText="Informática"
          disabled={loading}
        />

        <Separator />
        <AdminItemInfoForm
          form={form}
          setForm={setForm}
          errors={errors}
          setErrors={setErrors}
          loading={loading}
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
