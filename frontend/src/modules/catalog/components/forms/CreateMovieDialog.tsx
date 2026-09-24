import { useState } from "react";

import { Separator } from "@chakra-ui/react";

import {
  CustomDateInput,
  CustomInput,
  CustomNumberInput,
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
  useCreateMovie,
  useMovie,
  useMovieForm,
  useUpdateMovie,
} from "../../hooks";
import {
  FORMATS_OPTIONS,
  INITIAL_MOVIE,
  INITIAL_MOVIE_ERRORS,
  type MovieErrors,
} from "../../types/movie";
import type { CreateItemDialogProps } from "../../types";
import {
  MAX_LENGTH,
  validateMovieForm,
} from "../../validations/movie.validations";
import {
  AdminItemInfoForm,
  CreateSagaDialog,
  ItemImageInput,
  SagaSelect,
} from "..";

export function CreateMovieDialog({
  isOpen,
  setIsOpen,
  itemId,
}: CreateItemDialogProps) {
  const { data: movieToUpdate, isLoading: isMovieToEditLoading } =
    useMovie(itemId);
  const { form, setForm } = useMovieForm(
    itemId,
    movieToUpdate,
    isMovieToEditLoading,
  );
  const [errors, setErrors] = useState<MovieErrors>(INITIAL_MOVIE_ERRORS);
  const [image, setImage] = useState<File | null>(null);

  function resetForm() {
    setForm(INITIAL_MOVIE);
    setErrors(INITIAL_MOVIE_ERRORS);
    setImage(null);
  }

  const { mutateAsync: createMovie, isPending: submitting } = useCreateMovie(
    form,
    image,
    setErrors,
    setIsOpen,
    resetForm,
  );
  const { mutateAsync: updateMovie, isPending: updating } = useUpdateMovie(
    itemId,
    form,
    image,
    setErrors,
    setIsOpen,
    resetForm,
  );

  const loading = submitting || updating;
  const [sagaDialogOpen, setSagaDialogOpen] = useState(false);
  const [categoryDialogOpen, setCategoryDialogOpen] = useState(false);

  const handleFormatChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "format", form, setErrors, setForm);

  async function handleSubmit() {
    const isValid = validateMovieForm(form, setErrors);

    if (!isValid) return;

    if (itemId) {
      await updateMovie();
    } else {
      await createMovie();
    }
  }

  if (itemId && isMovieToEditLoading) {
    return null; // Esperando a que se cargue la película a editar
  }

  return (
    <>
      <FormDialog
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        title={
          movieToUpdate ? `Editando "${movieToUpdate.name}"` : "Crear película"
        }
        handleSubmit={handleSubmit}
        submitButtonText={movieToUpdate ? "Actualizar" : "Crear"}
        resetForm={resetForm}
      >
        <ItemImageInput
          image={image}
          setImage={setImage}
          loading={loading}
          placeholder={PLACEHOLDER.MOVIE}
          disabled={loading}
          imageUrl={movieToUpdate?.imageUrl}
        />

        <CustomInput
          label="Título"
          name="name"
          placeholder="Introduce el título..."
          required
          error={errors.name ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.NAME}
          defaultValue={form.name}
          value={form.name}
          disabled={loading}
        />
        <SagaSelect
          form={form}
          setErrors={setErrors}
          setForm={setForm}
          onCreateSaga={() => setSagaDialogOpen(true)}
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
          placeholder="Proporciona una sinopsis o descripción de la película"
          error={errors.description ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          textarea
          maxInputHeight="240px"
          maxLength={MAX_LENGTH.DESCRIPTION}
          defaultValue={form.description}
          value={form.description}
          disabled={loading}
        />
        <CustomSelect
          label="Formato"
          name="format"
          options={FORMATS_OPTIONS}
          onValueChange={handleFormatChange}
          placeholder="Selecciona el formato"
          value={[form.format]}
          defaultValue={[form.format]}
          error={errors.format ?? ""}
          required
          disabled={loading}
        />
        <CustomNumberInput
          label="Número de discos"
          defaultValue={form.numberOfDiscs}
          required
          min={1}
          max={20}
          onChange={(value: number) =>
            setForm((prev) => ({ ...prev, numberOfDiscs: value }))
          }
          error={errors.numberOfDiscs ?? ""}
          disabled={loading}
          value={form.numberOfDiscs}
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
          defaultValue={form.trailerUrl}
          value={form.trailerUrl}
          disabled={loading}
        />
        <SectionSelect
          form={form}
          setForm={setForm}
          errors={errors}
          setErrors={setErrors}
          defaultValueText="Arte"
          disabled={loading}
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
      <CreateSagaDialog
        isOpen={sagaDialogOpen}
        setIsOpen={setSagaDialogOpen}
        setSaga={(sagaName) => setForm((prev) => ({ ...prev, sagaName }))}
      />
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
