import { useState } from "react";
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
  CustomDateInput,
  FormDialog,
  UploadBox,
  toaster,
} from "@/modules/core/components";
import {
  CategoriesSelect,
  CreateCategoryDialog,
} from "@/modules/categories/components";
import { SectionSelect } from "@/modules/sections/components";
import {
  INITIAL_VIDEO_GAME,
  INITIAL_VIDEO_GAME_ERRORS,
  PLATFORM_OPTIONS,
  type VideoGameErrors,
  type VideoGameRequest,
} from "../types/videogame";
import { useCreateVideoGame } from "../hooks/useCreateVideoGame";
import {
  MAX_LENGTH,
  validateVideoGameForm,
} from "../validations/videogame.validations";
import { AdminItemInfoForm } from "./AdminItemInfoForm";

const VIDEOGAME_PLACEHOLDER =
  "https://res.cloudinary.com/dubz79y98/image/upload/v1788778962/movie_placeholder.jpg";

interface CreateVideoGameDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: (isOpen: boolean) => void;
}

export function CreateVideoGameDialog({
  isOpen,
  setIsOpen,
}: CreateVideoGameDialogProps) {
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
    const validationErrors = validateVideoGameForm(form);
    setErrors(validationErrors);
    if (Object.values(validationErrors).some(Boolean)) {
      toaster.create({
        title: "Error al crear videojuego",
        description:
          "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
        type: "error",
      });
      return;
    }
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
        <HStack
          align="stretch"
          w="100%"
          maxW="100%"
          maxH="200px"
          mb={image ? "60px" : ""}
        >
          <Box aspectRatio={2 / 3} h="auto" maxH="100%" flexShrink={0}>
            <Image
              src={image ? URL.createObjectURL(image) : VIDEOGAME_PLACEHOLDER}
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
                  Arrastra la <b>foto del videojuego</b>
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
          maxInputHeight="125px"
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
