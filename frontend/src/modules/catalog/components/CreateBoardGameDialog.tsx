import { useState } from "react";
import type { BoardGameErrors, BoardGameRequest } from "../types/boardgame";
import {
  BOARD_GAME_TYPES_OPTIONS,
  COMPLEXITIES_OPTIONS,
  INITIAL_BOARD_GAME,
  INITIAL_BOARD_GAME_ERRORS,
} from "../types/boardgame";
import { ITEM_CONDITIONS_OPTIONS } from "../types";
import { useCreateBoardGame } from "../hooks";
import { handleChange, handleSelectChange } from "@/modules/core/utils/utils";
import {
  Heading,
  HStack,
  Separator,
  VStack,
  Image,
  Box,
} from "@chakra-ui/react";
import {
  CustomInput,
  CustomSelect,
  CustomSwitch,
  CustomNumberInput,
  CustomDateInput,
  FormDialog,
  UploadBox,
  toaster,
} from "@/modules/core/components";
import { MAX_LENGTH as MAX_LENGTH_ITEM } from "../validations/item.validations";
import { validateBoardGameForm } from "../validations/boardgame.validations";
import {
  CategoriesSelect,
  CreateCategoryDialog,
} from "@/modules/categories/components";
import { SectionSelect } from "@/modules/sections/components";
import { BaseGameSelect } from "./BaseGameSelect";
import type { BoardGame } from "../types/boardgame";

const BOARD_GAME_PLACEHOLDER =
  "https://res.cloudinary.com/dubz79y98/image/upload/v1788778962/boardgame_placeholder.jpg";

interface CreateBoardGameDialogProps {
  readonly isOpen: boolean;
  readonly setIsOpen: (isOpen: boolean) => void;
  readonly token?: string | null;
  readonly onCreated?: (boardGame: BoardGame) => void;
  readonly allowBaseGame?: boolean;
}

export function CreateBoardGameDialog({
  isOpen,
  setIsOpen,
  token,
  onCreated,
  allowBaseGame = true,
}: CreateBoardGameDialogProps) {
  const [form, setForm] = useState<BoardGameRequest>(INITIAL_BOARD_GAME);
  const [errors, setErrors] = useState<BoardGameErrors>(
    INITIAL_BOARD_GAME_ERRORS,
  );
  const [image, setImage] = useState<File | null>(null);
  const { mutateAsync: createBoardGame, isPending: submitting } =
    useCreateBoardGame(form, image, setErrors, setIsOpen);
  const [categoryDialogOpen, setCategoryDialogOpen] = useState(false);
  const [baseGameDialogOpen, setBaseGameDialogOpen] = useState(false);

  const handleConditionChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "condition", form, setErrors, setForm);
  const handleComplexityChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "complexity", form, setErrors, setForm);
  const handleTypesChange = ({ value }: { value: string[] }) =>
    setForm((prev) => ({ ...prev, types: value as BoardGameRequest["types"] }));

  async function handleSubmit() {
    const validationErrors = validateBoardGameForm(form, token);
    setErrors(validationErrors);
    if (Object.values(validationErrors).some(Boolean)) {
      toaster.create({
        title: "Error al crear juego de mesa",
        description:
          "Se encontraron errores en el formulario. Por favor, corrígelos e inténtalo de nuevo.",
        type: "error",
      });
      return;
    }
    const createdBoardGame = await createBoardGame();
    if (createdBoardGame) {
      onCreated?.(createdBoardGame);
    }
  }

  return (
    <>
      <FormDialog
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        title="Crear juego de mesa"
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
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          textarea
          maxInputHeight="125px"
          maxLength={MAX_LENGTH_ITEM.DESCRIPTION}
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
        />

        <Separator />
        <Heading as="h2" size="md" mt={4}>
          Estado de conservación y disponibilidad
        </Heading>
        <CustomSelect
          label="Estado de conservación"
          name="condition"
          options={ITEM_CONDITIONS_OPTIONS}
          placeholder="Introduce el estado de conservación"
          required
          error={errors.condition ?? ""}
          onValueChange={handleConditionChange}
          value={[form.condition]}
        />
        <CustomInput
          label="Comentarios"
          name="comments"
          placeholder="Añade comentarios sobre el estado de conservación"
          textarea
          maxInputHeight="125px"
          maxLength={MAX_LENGTH_ITEM.COMMENTS}
          error={errors.comments ?? ""}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
        />
        <CustomSwitch
          checked={form.loanAvailable}
          onChange={(checked) =>
            setForm((prev) => ({ ...prev, loanAvailable: checked }))
          }
          label="Disponible para préstamo"
        />
        <CustomSwitch
          checked={form.publicated}
          onChange={(checked) =>
            setForm((prev) => ({ ...prev, publicated: checked }))
          }
          label="Visible en el catálogo"
        />

        <Separator />
        <Heading as="h2" size="md" mt={4}>
          Información sobre la compra
        </Heading>
        <CustomDateInput
          label="Fecha de compra"
          value={form.purchasedAt}
          error={errors.purchasedAt ?? ""}
          onChange={(value) =>
            setForm((prev) => ({ ...prev, purchasedAt: value }))
          }
          acceptsFutureDates={false}
        />
        <HStack>
          <CustomNumberInput
            label="Número de copias"
            defaultValue={form.copies}
            min={1}
            max={10}
            required
            error={errors.copies}
            onChange={(value) =>
              setForm((prev) => ({
                ...prev,
                copies: value,
                availableCopies: value,
              }))
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
            error={errors.price}
            onChange={(value) => setForm((prev) => ({ ...prev, price: value }))}
            isEuros
          />
        </HStack>
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
          token={token}
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
