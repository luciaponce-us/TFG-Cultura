import { useState } from "react";
import { useCreateRolGame, useRolSaga } from "../hooks";
import { handleChange, handleSelectChange } from "@/modules/core/utils/utils";
import { HStack, Separator, VStack, Image, Box } from "@chakra-ui/react";
import {
  CustomInput,
  CustomSelect,
  FormDialog,
  UploadBox,
  TextSecondary,
} from "@/modules/core/components";
import {
  INITIAL_ROL_GAME,
  INITIAL_ROL_GAME_ERRORS,
  ROL_BOOK_TYPES_OPTIONS,
  type RolGameErrors,
  type RolGameRequest,
} from "../types/rolgame";
import {
  MAX_LENGTH,
  validateRolGameForm,
} from "../validations/rolgame.validations";
import { useSectionNameContains } from "@/modules/sections/hooks/useSectionNameContains";
import { AdminItemInfoForm } from "./AdminItemInfoForm";

const BOOK_PLACEHOLDER =
  "https://res.cloudinary.com/dubz79y98/image/upload/v1788778962/book_placeholder.jpg";

interface CreateRolGameDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: (isOpen: boolean) => void;
  readonly sagaId: string;
}

export function CreateRolGameDialog({
  isOpen,
  setIsOpen,
  sagaId,
}: CreateRolGameDialogProps) {
  const sectionId = useSectionNameContains("rol")?.id;
  const [form, setForm] = useState<RolGameRequest>({
    ...INITIAL_ROL_GAME,
    sagaId: sagaId,
    sectionId: sectionId,
  });
  const [errors, setErrors] = useState<RolGameErrors>(INITIAL_ROL_GAME_ERRORS);
  const [image, setImage] = useState<File | null>(null);
  const {
    mutateAsync: createRolGame,
    isPending: submitting,
    isError: isCreateRolGameError,
  } = useCreateRolGame(form, image, setErrors, setIsOpen);
  const { data: rolSaga } = useRolSaga(sagaId);

  const handleTypeChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "type", form, setErrors, setForm);

  async function handleSubmit() {
    console.log("Submitting form:", form);
    const errors = validateRolGameForm(form);
    console.error("Validation errors:", errors);
    setErrors(errors);
    if (Object.values(errors).some(Boolean)) {
      return;
    }
    await createRolGame();
    if (!isCreateRolGameError) {
      setIsOpen(false);
    }
  }

  return (
    <FormDialog
      isOpen={isOpen}
      setIsOpen={setIsOpen}
      title="Crear juego de rol"
      handleSubmit={async () => await handleSubmit()}
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
            src={image ? URL.createObjectURL(image) : BOOK_PLACEHOLDER}
            alt="Foto del juego de rol"
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
                Arrastra la <b>foto del juego de rol</b>
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
        label="Nombre del libro"
        name="name"
        placeholder="Introduce el nombre del libro del juego de rol..."
        required
        error={errors.name ?? ""}
        onChange={(e) => handleChange(e, form, setErrors, setForm)}
        maxLength={MAX_LENGTH.NAME}
      />

      <CustomSelect
        label="Saga"
        options={rolSaga ? [{ value: rolSaga.id, label: rolSaga.name }] : []}
        placeholder="Cargando..."
        defaultValue={[form?.sagaId]}
        disabled
        required
      />
      <TextSecondary>
        La saga se asignará automáticamente según la página en la que te
        encuentres.
      </TextSecondary>

      <CustomSelect
        label="Tipo de libro del juego de rol"
        name="type"
        options={ROL_BOOK_TYPES_OPTIONS}
        onValueChange={handleTypeChange}
        placeholder="Selecciona el tipo de libro de juego de rol"
        defaultValue={[form?.type]}
      />

      <CustomInput
        label="Descripción"
        name="description"
        placeholder="Introduce la descripción del libro del juego de rol..."
        error={errors.description ?? ""}
        onChange={(e) => handleChange(e, form, setErrors, setForm)}
        textarea
        maxInputHeight="125px"
        maxLength={MAX_LENGTH.DESCRIPTION}
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
  );
}
