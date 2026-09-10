import { useState } from "react";
import { Box, Heading, HStack, Image, Separator, VStack } from "@chakra-ui/react";
import {
  CategoriesSelect,
  CreateCategoryDialog,
} from "@/modules/categories/components";
import { SectionSelect } from "@/modules/sections/components";
import {
  CustomDateInput,
  CustomButton,
  CustomInput,
  CustomNumberInput,
  CustomSelect,
  CustomSwitch,
  FormDialog,
  UploadBox,
  toaster,
  TextSecondary,
} from "@/modules/core/components";
import { IconPlus, IconTrash } from "@tabler/icons-react";
import { handleChange, handleSelectChange } from "@/modules/core/utils/utils";
import { useCreateSeries } from "../hooks";
import {
  FORMATS_OPTIONS,
} from "../types/movie";
import { ITEM_CONDITIONS_OPTIONS } from "../types";
import {
  INITIAL_SERIES,
  INITIAL_SERIES_ERRORS,
  SERIES_STATUSES_OPTIONS,
  type SeriesErrors,
  type SeriesRequest,
} from "../types/series";
import { MAX_LENGTH as MAX_LENGTH_ITEM } from "../validations/item.validations";
import {
  MAX_LENGTH as MAX_LENGTH_SERIES,
  validateSeriesForm,
} from "../validations/series.validations";

const SERIES_PLACEHOLDER =
  "https://res.cloudinary.com/dubz79y98/image/upload/v1788778962/movie_placeholder.jpg";

interface CreateSeriesDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: (isOpen: boolean) => void;
  readonly token?: string | null;
}

export function CreateSeriesDialog({
  isOpen,
  setIsOpen,
  token,
}: CreateSeriesDialogProps) {
  const [form, setForm] = useState<SeriesRequest>(INITIAL_SERIES);
  const [errors, setErrors] = useState<SeriesErrors>(INITIAL_SERIES_ERRORS);
  const [image, setImage] = useState<File | null>(null);
  const [categoryDialogOpen, setCategoryDialogOpen] = useState(false);
  const { mutateAsync: createSeries, isPending: submitting } = useCreateSeries(
    form,
    image,
    setErrors,
    setIsOpen,
  );

  const handleFormatChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "format", form, setErrors, setForm);
  const handleStatusChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "status", form, setErrors, setForm);
  const handleConditionChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "condition", form, setErrors, setForm);

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
    const validationErrors = validateSeriesForm(form, token);
    setErrors(validationErrors);
    if (Object.values(validationErrors).some(Boolean)) {
      toaster.create({
        title: "Error al crear serie",
        description: "Corrige los errores del formulario e inténtalo de nuevo.",
        type: "error",
      });
      return;
    }
    await createSeries();
  }

  return (
    <>
    <FormDialog
      isOpen={isOpen}
      setIsOpen={setIsOpen}
      title="Crear serie"
      handleSubmit={handleSubmit}
      submitButtonText="Crear"
    >
      <HStack align="stretch" w="100%" maxH="200px" mb={image ? "60px" : ""}>
        <Box aspectRatio={2 / 3} h="auto" maxH="100%" flexShrink={0}>
          <Image
            src={image ? URL.createObjectURL(image) : SERIES_PLACEHOLDER}
            alt="Foto de la serie"
            w="100%"
            h="100%"
            objectFit="cover"
            borderRadius="md"
          />
        </Box>
        <VStack flex={1} minW={0}>
          <UploadBox
            text={<>Arrastra la <b>foto de la serie</b></>}
            secondaryText="JPG o PNG, tamaño no superior a 2MB"
            fileType="image/*"
            onFileChange={setImage}
            disabled={submitting}
          />
        </VStack>
      </HStack>

      <CustomInput
        label="Título"
        name="name"
        placeholder="Introduce el título..."
        required
        error={errors.name ?? ""}
        onChange={(event) => handleChange(event, form, setErrors, setForm)}
        maxLength={MAX_LENGTH_ITEM.NAME}
      />
      <CategoriesSelect
        form={form}
        setForm={setForm}
        onCreateCategory={() => setCategoryDialogOpen(true)}
      />
      <CustomInput
        label="Sinopsis"
        name="description"
        placeholder="Proporciona una sinopsis o descripción de la serie"
        error={errors.description ?? ""}
        onChange={(event) => handleChange(event, form, setErrors, setForm)}
        textarea
        maxInputHeight="125px"
        maxLength={MAX_LENGTH_ITEM.DESCRIPTION}
      />
      <CustomSelect
        label="Formato"
        name="format"
        options={FORMATS_OPTIONS}
        onValueChange={handleFormatChange}
        placeholder="Selecciona el formato"
        value={[form.format]}
        error={errors.format ?? ""}
        required
      />
      <CustomNumberInput
        label="Número de discos"
        defaultValue={form.numberOfDiscs}
        min={1}
        max={20}
        required
        onChange={(value: number) => setForm((previous) => ({ ...previous, numberOfDiscs: value }))}
      />
      <Heading as="h2" size="md" mt={4}>Información de la serie</Heading>
      <CustomDateInput
        label="Fecha de estreno"
        value={form.releaseDate}
        error={errors.releaseDate ?? ""}
        onChange={(value) => setForm((previous) => ({ ...previous, releaseDate: value }))}
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
      />
      <VStack align="stretch" gap={4}>
        <HStack justify="space-between" align="center">
          <Heading as="h2" size="md">Temporadas</Heading>
          
          <CustomButton
            type="button"
            onClick={addSeason}
            disabled={submitting}
          >
            <IconPlus />
            Añadir temporada
          </CustomButton>
        </HStack>
        <TextSecondary>Añade las temporadas que estén en este disco.</TextSecondary>
        {form.seasons.map((season, index) => (
          <VStack key={index} align="stretch" gap={2}>
            <HStack justify="space-between" align="center">
              <Heading as="h3" size="sm">Temporada {index + 1}</Heading>
              <CustomButton
                type="button"
                color="transparent"
                aria-label={`Eliminar temporada ${index + 1}`}
                title={`Eliminar temporada ${index + 1}`}
                onClick={() => removeSeason(index)}
                disabled={form.seasons.length <= 1}
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
                      itemIndex === index ? { ...item, seasonNumber: value } : item,
                    ),
                  }))
                }
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
                      itemIndex === index ? { ...item, seasonPart: value } : item,
                    ),
                  }))
                }
              />
            </HStack>
            <CustomInput
              label="Tráiler de la temporada"
              name={`seasonTrailerUrl-${index}`}
              placeholder="https://www.youtube.com/embed/..."
              maxLength={MAX_LENGTH_SERIES.TRAILER_URL}
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
            />
          </VStack>
        ))}
        {errors.seasons && <Box color="red.500">{errors.seasons}</Box>}
      </VStack>
      <SectionSelect form={form} setForm={setForm} errors={errors} setErrors={setErrors} />

      <Separator />
      <Heading as="h2" size="md" mt={4}>Estado de conservación y disponibilidad</Heading>
      <CustomSelect
        label="Estado de conservación"
        name="condition"
        options={ITEM_CONDITIONS_OPTIONS}
        onValueChange={handleConditionChange}
        placeholder="Selecciona el estado de conservación"
        value={[form.condition]}
        error={errors.condition ?? ""}
        required
      />
      <CustomInput
        label="Comentarios"
        name="comments"
        placeholder="Añade comentarios sobre el estado de conservación"
        textarea
        maxInputHeight="125px"
        maxLength={MAX_LENGTH_ITEM.COMMENTS}
        onChange={(event) => handleChange(event, form, setErrors, setForm)}
      />
      <CustomSwitch
        checked={form.loanAvailable}
        onChange={(checked) => setForm((previous) => ({ ...previous, loanAvailable: checked }))}
        label="Disponible para préstamo"
      />
      <CustomSwitch
        checked={form.publicated}
        onChange={(checked) => setForm((previous) => ({ ...previous, publicated: checked }))}
        label="Visible en el catálogo"
      />
      <Separator />
      <Heading as="h2" size="md" mt={4}>Información sobre la compra</Heading>
      <CustomDateInput
        label="Fecha de compra"
        value={form.purchasedAt}
        error={errors.purchasedAt ?? ""}
        onChange={(value) => setForm((previous) => ({ ...previous, purchasedAt: value }))}
        acceptsFutureDates={false}
      />
      <HStack>
        <CustomNumberInput
          label="Número de copias"
          defaultValue={form.copies}
          min={1}
          max={10}
          required
          onChange={(value: number) =>
            setForm((previous) => ({ ...previous, copies: value, availableCopies: value }))
          }
        />
        <CustomNumberInput
          label="Precio de compra"
          defaultValue={form.price}
          min={0}
          max={1000}
          step={0.01}
          allowMouseWheel
          disabled={submitting}
          onChange={(value: number) => setForm((previous) => ({ ...previous, price: value }))}
          isEuros
        />
      </HStack>
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