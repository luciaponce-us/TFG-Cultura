import { useState } from "react";
import {
  BOARD_GAME_TYPES_OPTIONS,
  COMPLEXITIES_OPTIONS,
  INITIAL_BOARD_GAME_ERRORS,
  type BoardGame,
  type BoardGameErrors,
  type BoardGameRequest,
} from "../types/boardgame";
import {
  useBoardGame,
  useBoardGameForm,
  useCreateBoardGame,
  useUpdateBoardGame,
} from "../hooks";
import { handleChange, handleSelectChange } from "@/modules/core/utils/utils";
import { HStack, Separator, VStack, Image, Box } from "@chakra-ui/react";
import {
  CustomInput,
  CustomSelect,
  CustomNumberInput,
  FormDialog,
  UploadBox,
} from "@/modules/core/components";
import {
  validateBoardGameForm,
  MAX_LENGTH,
} from "../validations/boardgame.validations";
import {
  CategoriesSelect,
  CreateCategoryDialog,
} from "@/modules/categories/components";
import { SectionSelect } from "@/modules/sections/components";
import { BaseGameSelect } from "./BaseGameSelect";
import { AdminItemInfoForm } from "./AdminItemInfoForm";

const BOARD_GAME_PLACEHOLDER =
  "https://res.cloudinary.com/dubz79y98/image/upload/v1788778962/boardgame_placeholder.jpg";

interface CreateBoardGameDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: (isOpen: boolean) => void;
  readonly onCreated?: (boardGame: BoardGame) => void;
  readonly allowBaseGame?: boolean;
  readonly boardGameId?: string;
}

export function CreateBoardGameDialog({
  isOpen,
  setIsOpen,
  onCreated,
  allowBaseGame = true,
  boardGameId,
}: CreateBoardGameDialogProps) {
  const { data: boardGameToUpdate } = useBoardGame(boardGameId);
  const { form, setForm } = useBoardGameForm(boardGameId, boardGameToUpdate);
  const [errors, setErrors] = useState<BoardGameErrors>(
    INITIAL_BOARD_GAME_ERRORS,
  );
  const [image, setImage] = useState<File | null>(null);
  const { mutateAsync: createBoardGame, isPending: submitting } =
    useCreateBoardGame(form, image, setErrors, setIsOpen);
  const { mutateAsync: updateBoardGame, isPending: updating } =
    useUpdateBoardGame(boardGameId, form, image, setErrors, setIsOpen);
  const loading = boardGameId ? updating : submitting;
  const [categoryDialogOpen, setCategoryDialogOpen] = useState(false);
  const [baseGameDialogOpen, setBaseGameDialogOpen] = useState(false);

  const handleComplexityChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "complexity", form, setErrors, setForm);
  const handleTypesChange = ({ value }: { value: string[] }) =>
    setForm((prev) => ({ ...prev, types: value as BoardGameRequest["types"] }));

  async function handleSubmit() {
    validateBoardGameForm(form, setErrors);

    if (boardGameId) {
      await updateBoardGame();
    } else {
      const createdBoardGame = await createBoardGame();
      if (createdBoardGame) {
        onCreated?.(createdBoardGame);
      }
    }
  }

  return (
    <>
      <FormDialog
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        title={
          boardGameToUpdate
            ? `Editando ${boardGameToUpdate.name}`
            : "Crear juego de mesa"
        }
        handleSubmit={handleSubmit}
        submitButtonText={boardGameId ? "Actualizar" : "Crear"}
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
              src={image ? URL.createObjectURL(image) : BOARD_GAME_PLACEHOLDER}
              alt="Foto del juego de mesa"
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
                  Arrastra la <b>foto del juego de mesa</b>
                </>
              }
              secondaryText="JPG o PNG, tamaño no superior a 2MB"
              fileType="image/*"
              onFileChange={setImage}
              disabled={loading}
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
          defaultValue={form.name}
        />
        <CategoriesSelect
          form={form}
          setForm={setForm}
          error={errors.categoriesIds}
          onCreateCategory={() => setCategoryDialogOpen(true)}
        />
        <CustomInput
          label="Descripción"
          name="description"
          placeholder="Proporciona una descripción del juego de mesa"
          error={errors.description ?? ""}
          defaultValue={form.description}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          textarea
          maxInputHeight="240px"
          maxLength={MAX_LENGTH.DESCRIPTION}
        />
        {allowBaseGame && (
          <BaseGameSelect
            form={form}
            setForm={setForm}
            error={errors.baseGameId}
            onCreateBaseGame={() => setBaseGameDialogOpen(true)}
          />
        )}
        <HStack w="100%" align="start">
          <CustomNumberInput
            label="Jugadores mínimos"
            defaultValue={form.minPlayers}
            min={1}
            required
            error={errors.minPlayers}
            onChange={(value) =>
              setForm((prev) => ({ ...prev, minPlayers: value }))
            }
          />
          <CustomNumberInput
            label="Jugadores máximos"
            defaultValue={form.maxPlayers}
            min={1}
            required
            error={errors.maxPlayers}
            onChange={(value) =>
              setForm((prev) => ({ ...prev, maxPlayers: value }))
            }
          />
        </HStack>
        <CustomNumberInput
          label="Tiempo de juego (minutos)"
          defaultValue={form.playTime}
          min={1}
          required
          error={errors.playTime}
          onChange={(value) =>
            setForm((prev) => ({ ...prev, playTime: value }))
          }
        />
        <CustomSelect
          label="Complejidad"
          name="complexity"
          options={COMPLEXITIES_OPTIONS}
          onValueChange={handleComplexityChange}
          placeholder="Selecciona la complejidad"
          value={[form.complexity]}
          error={errors.complexity}
          required
        />
        <CustomSelect
          label="Tipos de juego"
          name="types"
          options={BOARD_GAME_TYPES_OPTIONS}
          onValueChange={handleTypesChange}
          placeholder="Selecciona los tipos"
          value={form.types}
          error={errors.types}
          required
          multiple
        />
        <SectionSelect
          form={form}
          setForm={setForm}
          errors={errors}
          setErrors={setErrors}
          defaultValueText="Juegos de mesa"
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
          setForm((prev) => ({
            ...prev,
            categoriesIds: [...prev.categoriesIds, category.id],
          }))
        }
      />
      {allowBaseGame && (
        <CreateBoardGameDialog
          isOpen={baseGameDialogOpen}
          setIsOpen={setBaseGameDialogOpen}
          allowBaseGame={false}
          onCreated={(boardGame) => {
            setForm((prev) => ({ ...prev, baseGameId: boardGame.id }));
            setBaseGameDialogOpen(false);
            onCreated?.(boardGame);
          }}
        />
      )}
    </>
  );
}
