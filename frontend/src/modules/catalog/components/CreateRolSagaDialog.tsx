import { useState } from "react";
import {
  GAME_MASTERS_OPTIONS,
  INITIAL_ROL_SAGA,
  INITIAL_ROL_SAGA_ERRORS,
  type RolSagaErrors,
  type RolSagaRequest,
} from "../types/rolgame";
import { useCreateRolSaga } from "../hooks";
import { FormDialog } from "@/modules/core/components/FormDialog";
import { Box, HStack, VStack, Image } from "@chakra-ui/react";
import {
  CustomInput,
  CustomSelect,
  toaster,
  UploadBox,
} from "@/modules/core/components";
import {
  CategoriesSelect,
  CreateCategoryDialog,
} from "@/modules/categories/components";
import { handleChange, handleSelectChange } from "@/modules/core/utils/utils";
import {
  MAX_LENGTH,
  validateRolSagaForm,
} from "../validations/rolsaga.validations";
import { SectionSelect } from "@/modules/sections/components";

interface CreateRolSagaDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: (isOpen: boolean) => void;
}

const ROL_SAGA_PLACEHOLDER =
  "https://res.cloudinary.com/dubz79y98/image/upload/v1776288595/boardgame_placeholder.jpg";

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
    const errors = validateRolSagaForm(form);
    console.error("RolSagaForm errors:", errors);

    if (Object.keys(errors).length > 0) {
      setErrors(errors);
      toaster.create({
        title: "Error al crear saga de rol",
        description:
          "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
        type: "error",
      });
      return;
    }

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
              src={image ? URL.createObjectURL(image) : ROL_SAGA_PLACEHOLDER}
              alt="Foto de la saga de juegos de rol"
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
                  Arrastra la <b>foto de la saga de juegos de rol</b>
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
