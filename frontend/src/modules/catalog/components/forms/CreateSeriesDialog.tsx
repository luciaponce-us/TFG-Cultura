import { useState } from "react";

import { IconPlus, IconTrash } from "@tabler/icons-react";
import { Box, Heading, HStack, Separator, VStack } from "@chakra-ui/react";

import {
  CategoriesSelect,
  CreateCategoryDialog,
} from "@/modules/categories/components";
import { SectionSelect } from "@/modules/sections/components";
import {
  CustomButton,
  CustomDateInput,
  CustomInput,
  CustomNumberInput,
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
  useCreateSeries,
  useSerie,
  useSeriesForm,
  useUpdateSeries,
} from "../../hooks";
import {
  FORMATS_OPTIONS,
  INITIAL_SERIES,
  INITIAL_SERIES_ERRORS,
  SERIES_STATUSES_OPTIONS,
  type SeriesErrors,
} from "../../types/series";
import {
  MAX_LENGTH,
  validateSeriesForm,
} from "../../validations/series.validations";
import type { CreateItemDialogProps } from "../../types";
import { ItemImageInput, AdminItemInfoForm } from "..";

export function CreateSeriesDialog({
  isOpen,
  setIsOpen,
  itemId,
}: CreateItemDialogProps) {
  const { data: seriesToUpdate, isLoading: isSeriesToEditLoading } =
    useSerie(itemId);
  const { form, setForm } = useSeriesForm(
    itemId,
    seriesToUpdate,
    isSeriesToEditLoading,
  );
  const [errors, setErrors] = useState<SeriesErrors>(INITIAL_SERIES_ERRORS);
  const [image, setImage] = useState<File | null>(null);
  function resetForm() {
    setForm(INITIAL_SERIES);
    setErrors(INITIAL_SERIES_ERRORS);
    setImage(null);
  }
  const [categoryDialogOpen, setCategoryDialogOpen] = useState(false);
  const { mutateAsync: createSeries, isPending: submitting } = useCreateSeries(
    form,
    image,
    setErrors,
    setIsOpen,
    resetForm,
  );
  const { mutateAsync: updateSeries, isPending: updating } = useUpdateSeries(
    itemId,
    form,
    image,
    setErrors,
    setIsOpen,
    resetForm,
  );
  const loading = submitting || updating;

  const handleFormatChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "format", form, setErrors, setForm);
  const handleStatusChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "status", form, setErrors, setForm);

  function addSeason() {
    setForm((previous) => ({
      ...previous,
      seasons: [
        ...previous.seasons,
        {
          seasonNumber: previous.seasons.length + 1,
          seasonPart: 0,
          trailerUrl: "",
        },
      ],
    }));
  }

  function removeSeason(indexToRemove: number) {
    setForm((previous) => {
      if (previous.seasons.length <= 1) return previous;

      const seasons = previous.seasons.filter(
        (_, index) => index !== indexToRemove,
      );
      return {
        ...previous,
        seasons,
      };
    });
  }

  async function handleSubmit() {
    const isValid = validateSeriesForm(form, setErrors);
    if (!isValid) return;
    if (itemId) {
      await updateSeries();
    } else {
      await createSeries();
    }
  }

  if (itemId && isSeriesToEditLoading) {
    return null; // Esperando a que se cargue la serie a editar
  }

  return (
    <>
      <FormDialog
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        title={
          seriesToUpdate ? `Editando "${seriesToUpdate.name}"` : "Crear serie"
        }
        handleSubmit={handleSubmit}
        submitButtonText={itemId ? "Guardar" : "Crear"}
        resetForm={resetForm}
      >
        <ItemImageInput
          image={image}
          setImage={setImage}
          loading={isSeriesToEditLoading}
          placeholder={PLACEHOLDER.SERIES}
          disabled={loading}
          imageUrl={seriesToUpdate?.imageUrl}
        />

        <CustomInput
          label="Título"
          name="name"
          placeholder="Introduce el título..."
          required
          error={errors.name ?? ""}
          onChange={(event) => handleChange(event, form, setErrors, setForm)}
          maxLength={MAX_LENGTH.NAME}
          value={form.name}
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
          placeholder="Proporciona una sinopsis o descripción de la serie"
          error={errors.description ?? ""}
          onChange={(event) => handleChange(event, form, setErrors, setForm)}
          textarea
          maxInputHeight="240px"
          maxLength={MAX_LENGTH.DESCRIPTION}
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
          min={1}
          max={20}
          required
          onChange={(value: number) =>
            setForm((previous) => ({ ...previous, numberOfDiscs: value }))
          }
          error={errors.numberOfDiscs ?? ""}
          value={form.numberOfDiscs}
          disabled={loading}
        />
        <Separator />
        <Heading as="h2" size="md" mt={4}>
          Información de la serie
        </Heading>
        <CustomDateInput
          label="Fecha de estreno"
          value={form.releaseDate}
          error={errors.releaseDate ?? ""}
          onChange={(value) =>
            setForm((previous) => ({ ...previous, releaseDate: value }))
          }
          disabled={loading}
        />
        <CustomNumberInput
          label="Número de temporadas"
          defaultValue={form.numberOfSeasons}
          min={1}
          max={1000}
          required
          onChange={(value: number) =>
            setForm((previous) => ({ ...previous, numberOfSeasons: value }))
          }
          error={errors.numberOfSeasons ?? ""}
          value={form.numberOfSeasons}
          disabled={loading}
        />
        <CustomSelect
          label="Estado de la serie"
          name="status"
          options={SERIES_STATUSES_OPTIONS}
          onValueChange={handleStatusChange}
          placeholder="Selecciona el estado de la serie"
          value={[form.status]}
          error={errors.status ?? ""}
          required
          disabled={loading}
        />
        <Separator />
        <VStack align="stretch" gap={4}>
          <HStack justify="space-between" align="center">
            <Heading as="h2" size="md">
              Temporadas
            </Heading>

            <CustomButton type="button" onClick={addSeason} disabled={loading}>
              <IconPlus />
              Añadir temporada
            </CustomButton>
          </HStack>
          <TextSecondary>
            Añade las temporadas que estén en este disco.
          </TextSecondary>
          {form.seasons.map((season, index) => (
            <VStack key={index} align="stretch" gap={2}>
              <HStack justify="space-between" align="center">
                <Heading as="h3" size="sm">
                  Temporada {index + 1}
                </Heading>
                <CustomButton
                  type="button"
                  color="transparent"
                  aria-label={`Eliminar temporada ${index + 1}`}
                  title={`Eliminar temporada ${index + 1}`}
                  onClick={() => removeSeason(index)}
                  disabled={loading || form.seasons.length <= 1}
                >
                  <IconTrash size={18} />
                </CustomButton>
              </HStack>
              <HStack align="start">
                <CustomNumberInput
                  label="Número"
                  defaultValue={season.seasonNumber}
                  min={0}
                  max={1000}
                  onChange={(value: number) =>
                    setForm((previous) => ({
                      ...previous,
                      seasons: previous.seasons.map((item, itemIndex) =>
                        itemIndex === index
                          ? { ...item, seasonNumber: value }
                          : item,
                      ),
                    }))
                  }
                  disabled={loading}
                />
                <CustomNumberInput
                  label="Parte"
                  defaultValue={season.seasonPart ?? 0}
                  min={0}
                  max={10}
                  onChange={(value: number) =>
                    setForm((previous) => ({
                      ...previous,
                      seasons: previous.seasons.map((item, itemIndex) =>
                        itemIndex === index
                          ? { ...item, seasonPart: value }
                          : item,
                      ),
                    }))
                  }
                  disabled={loading}
                />
              </HStack>
              <CustomInput
                label="Tráiler de la temporada"
                name={`seasonTrailerUrl-${index}`}
                placeholder="https://www.youtube.com/embed/..."
                maxLength={MAX_LENGTH.TRAILER_URL}
                defaultValue={season.trailerUrl ?? ""}
                onChange={(event) =>
                  setForm((previous) => ({
                    ...previous,
                    seasons: previous.seasons.map((item, itemIndex) =>
                      itemIndex === index
                        ? { ...item, trailerUrl: event.target.value }
                        : item,
                    ),
                  }))
                }
                disabled={loading}
              />
            </VStack>
          ))}
          {errors.seasons && <Box color="red.500">{errors.seasons}</Box>}
        </VStack>
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
      <CreateCategoryDialog
        isOpen={categoryDialogOpen}
        setIsOpen={setCategoryDialogOpen}
        onCategoryCreated={(category) =>
          setForm((previous) => ({
            ...previous,
            categoriesIds: [...previous.categoriesIds, category.id],
          }))
        }
      />
    </>
  );
}
