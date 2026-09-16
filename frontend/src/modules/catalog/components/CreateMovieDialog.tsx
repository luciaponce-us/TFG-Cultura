import { useState } from "react";
import type { MovieErrors, MovieRequest } from "../types/movie";
import {
  FORMATS_OPTIONS,
  INITIAL_MOVIE,
  INITIAL_MOVIE_ERRORS,
} from "../types/movie";
import { useCreateMovie } from "../hooks";
import { handleChange, handleSelectChange } from "@/modules/core/utils/utils";
import {
  HStack,
  Separator,
  VStack,
  Image,
  Box,
} from "@chakra-ui/react";
import {
  CustomInput,
  CustomSelect,
  CustomNumberInput,
  CustomDateInput,
  FormDialog,
  UploadBox,
  toaster,
} from "@/modules/core/components";
import { MAX_LENGTH as MAX_LENGTH_ITEM } from "../validations/item.validations";
import {
  MAX_LENGTH as MAX_LENGTH_MOVIE,
  validateMovieForm,
} from "../validations/movie.validations";
import { CreateSagaDialog, SagaSelect } from "./";
import {
  CategoriesSelect,
  CreateCategoryDialog,
} from "@/modules/categories/components";
import { SectionSelect } from "@/modules/sections/components";
import { AdminItemInfoForm } from "./AdminItemInfoForm";

const MOVIE_PLACEHOLDER =
  "https://res.cloudinary.com/dubz79y98/image/upload/v1788778962/movie_placeholder.jpg";

interface CreateMovieDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: (isOpen: boolean) => void;
}

export function CreateMovieDialog({
  isOpen,
  setIsOpen
}: CreateMovieDialogProps) {
  const [form, setForm] = useState<MovieRequest>(INITIAL_MOVIE);
  const [errors, setErrors] = useState<MovieErrors>(INITIAL_MOVIE_ERRORS);
  const [image, setImage] = useState<File | null>(null);
  const { mutateAsync: createMovie, isPending: submitting } = useCreateMovie(
    form,
    image,
    setErrors,
    setIsOpen,
  );
  const [sagaDialogOpen, setSagaDialogOpen] = useState(false);
  const [categoryDialogOpen, setCategoryDialogOpen] = useState(false);

  const handleFormatChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "format", form, setErrors, setForm);

  async function handleSubmit() {
    const validationErrors = validateMovieForm(form);
    setErrors(validationErrors);
    if (Object.values(validationErrors).some(Boolean)) {
      toaster.create({
        title: "Error al crear película",
        description:
          "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
        type: "error",
      });
      return;
    }
    await createMovie();
  }

  return (
    <>
      <FormDialog
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        title="Crear película"
        handleSubmit={handleSubmit}
        submitButtonText="Crear"
      >
        <HStack
          align="stretch"
          w="100%"
          maxW="100%"
          maxH="200px"
          mb={image ? "60px" : ""}
        >
          <Box aspectRatio={2 / 3} h="auto" maxH="100%" flexShrink={0}>
            <Image
              src={image ? URL.createObjectURL(image) : MOVIE_PLACEHOLDER}
              alt="Foto de la película"
              w="100%"
              h="100%"
              objectFit="cover"
              borderRadius="md"
            />
          </Box>
          <VStack flex={1} minW={0}>
            <UploadBox
              text={
                <>
                  Arrastra la <b>foto de la película</b>
                </>
              }
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
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          maxLength={MAX_LENGTH_ITEM.NAME}
        />
        <SagaSelect
          form={form}
          setErrors={setErrors}
          setForm={setForm}
          onCreateSaga={() => setSagaDialogOpen(true)}
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
          placeholder="Proporciona una sinopsis o descripción de la película"
          error={errors.description ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
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
          required
          min={1}
          max={20}
          onChange={(value: number) =>
            setForm((prev) => ({ ...prev, numberOfDiscs: value }))
          }
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
          maxLength={MAX_LENGTH_MOVIE.TRAILER_URL}
        />
        <SectionSelect
          form={form}
          setForm={setForm}
          errors={errors}
          setErrors={setErrors}
          defaultValueText="Arte"
        />

        <Separator />
        <AdminItemInfoForm
          form={form}
          setForm={setForm}
          errors={errors}
          setErrors={setErrors}
          loading={submitting}
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
