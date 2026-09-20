import { useState } from "react";

import { HStack, Separator } from "@chakra-ui/react";

import {
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
  useBoardGame,
  useBoardGameForm,
  useCreateBoardGame,
  useUpdateBoardGame,
} from "../hooks";
import {
  BOARD_GAME_TYPES_OPTIONS,
  COMPLEXITIES_OPTIONS,
  INITIAL_BOARD_GAME,
  INITIAL_BOARD_GAME_ERRORS,
  type BoardGame,
  type BoardGameErrors,
  type BoardGameRequest,
} from "../types/boardgame";
import type { CreateItemDialogProps } from "../types";
import {
  MAX_LENGTH,
  validateBoardGameForm,
} from "../validations/boardgame.validations";
import { AdminItemInfoForm, BaseGameSelect, ItemImageInput } from ".";

interface CreateBoardGameDialogProps extends CreateItemDialogProps {
  readonly onCreated?: (boardGame: BoardGame) => void;
  readonly allowBaseGame?: boolean;
}

export function CreateBoardGameDialog({
  isOpen,
  setIsOpen,
  onCreated,
  allowBaseGame = true,
  itemId,
}: CreateBoardGameDialogProps) {
  const isEdit = itemId !== undefined;
  const {
    data: boardGameToUpdate,
    isLoading: isLoadingBoardGameToUpdate,
  } = useBoardGame(itemId);

  const { form, setForm } = useBoardGameForm(itemId, boardGameToUpdate, isLoadingBoardGameToUpdate);
  const [errors, setErrors] = useState<BoardGameErrors>(
    INITIAL_BOARD_GAME_ERRORS,
  );
  const [image, setImage] = useState<File | null>(null);
  const { mutateAsync: createBoardGame, isPending: creating } =
    useCreateBoardGame(form, image, setErrors, setIsOpen);
  const { mutateAsync: updateBoardGame, isPending: updating } =
    useUpdateBoardGame(itemId, form, image, setErrors, setIsOpen);
  const submitting = isEdit ? updating : creating;

  const [categoryDialogOpen, setCategoryDialogOpen] = useState(false);
  const [baseGameDialogOpen, setBaseGameDialogOpen] = useState(false);

  function resetForm() {
    setForm(INITIAL_BOARD_GAME);
    setErrors(INITIAL_BOARD_GAME_ERRORS);
    setImage(null);
  }
  
  const handleComplexityChange = ({ value }: { value: string[] }) =>
    handleSelectChange(value, "complexity", form, setErrors, setForm);
  const handleTypesChange = ({ value }: { value: string[] }) =>
    setForm((prev) => ({ ...prev, types: value as BoardGameRequest["types"] }));

  async function handleSubmit() {
    const isValid = validateBoardGameForm(form, setErrors, isEdit);
    if (!isValid) return;
    
    if (itemId) {
      await updateBoardGame();
    } else {
      const createdBoardGame = await createBoardGame();
      if (createdBoardGame) {
        onCreated?.(createdBoardGame);
      }
    }
  }

  if (itemId && isLoadingBoardGameToUpdate) {
    return null;
  }

  return (
    <>
      <FormDialog
        key={boardGameToUpdate?.id ?? itemId ?? "new-board-game"}
        isOpen={isOpen}
        setIsOpen={setIsOpen}
        title={
          boardGameToUpdate
            ? `Editando ${boardGameToUpdate.name}`
            : "Crear juego de mesa"
        }
        handleSubmit={handleSubmit}
        submitButtonText={itemId ? "Actualizar" : "Crear"}
        resetForm={resetForm}
      >
        <ItemImageInput
          image={image}
          setImage={setImage}
          loading={submitting}
          placeholder={PLACEHOLDER.BOARDGAME}
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
          disabled={submitting}
        />
        <CategoriesSelect
          form={form}
          setForm={setForm}
          error={errors.categoriesIds}
          onCreateCategory={() => setCategoryDialogOpen(true)}
          disabled={submitting}
        />
        <CustomInput
          label="Descripción"
          name="description"
          placeholder="Proporciona una descripción del juego de mesa"
          error={errors.description ?? ""}
          value={form.description}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          textarea
          maxInputHeight="240px"
          maxLength={MAX_LENGTH.DESCRIPTION}
          disabled={submitting}
        />
        <Separator />
        {allowBaseGame && (
          <BaseGameSelect
            form={form}
            setForm={setForm}
            error={errors.baseGameId}
            onCreateBaseGame={() => setBaseGameDialogOpen(true)}
            disabled={submitting}
          />
        )}
        <Separator />
        <HStack w="100%" align="start">
          <CustomNumberInput
            label="Jugadores mínimos"
            value={form.minPlayers}
            min={1}
            required
            error={errors.minPlayers}
            onChange={(value) =>
              setForm((prev) => ({ ...prev, minPlayers: value }))
            }
            defaultValue={form.minPlayers}
            disabled={submitting}
          />
          <CustomNumberInput
            label="Jugadores máximos"
            value={form.maxPlayers}
            min={1}
            required
            error={errors.maxPlayers}
            onChange={(value) =>
              setForm((prev) => ({ ...prev, maxPlayers: value }))
            }
            defaultValue={form.maxPlayers}
            disabled={submitting}
          />
        </HStack>
        <CustomNumberInput
          label="Tiempo de juego (minutos)"
          value={form.playTime}
          min={1}
          required
          error={errors.playTime}
          onChange={(value) =>
            setForm((prev) => ({ ...prev, playTime: value }))
          }
          defaultValue={form.playTime}
          disabled={submitting}
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
          disabled={submitting}
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
          disabled={submitting}
        />
        <SectionSelect
          form={form}
          setForm={setForm}
          errors={errors}
          setErrors={setErrors}
          defaultValueText="Juegos de mesa"
          disabled={submitting}
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
