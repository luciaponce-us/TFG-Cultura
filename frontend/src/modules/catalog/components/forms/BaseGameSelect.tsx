import { useState } from "react";
import { Heading, HStack, Text, VStack } from "@chakra-ui/react";
import {
  CustomButton,
  CustomSearchBar,
  CustomSelect,
} from "@/modules/core/components";
import { useBoardGames } from "../../hooks";
import type { BoardGameRequest } from "../../types/boardgame";

interface BaseGameSelectProps {
  readonly form: BoardGameRequest;
  readonly setForm: React.Dispatch<React.SetStateAction<BoardGameRequest>>;
  readonly error?: string;
  readonly onCreateBaseGame: () => void;
  readonly disabled: boolean;
}

export function BaseGameSelect({
  form,
  setForm,
  error,
  onCreateBaseGame,
  disabled,
}: BaseGameSelectProps) {
  const [search, setSearch] = useState("");
  const { data, isLoading, isError } = useBoardGames(0, {
    nameContains: search,
    categoriesIds: [],
  });

  const baseGameOptions =
    data?.content
      .filter(
        (boardGame) => !boardGame.isExpansion && boardGame.baseGame === null,
      )
      .map((boardGame) => ({ value: boardGame.id, label: boardGame.name })) ??
    [];

  function handleSubmit() {
    onCreateBaseGame();
    setSearch("");
  }

  return (
    <VStack align="stretch" gap={3} w="100%">
      <Heading as="h2" size="md">
        Juego base
      </Heading>
      <CustomSearchBar
        value={search}
        onChange={(event) => setSearch(event.currentTarget.value)}
        placeholder="Buscar juego base..."
        disabled={disabled}
      />
      <CustomSelect
        label="Selecciona el juego base"
        name="baseGameId"
        options={baseGameOptions}
        placeholder="Selecciona un juego base"
        value={form.baseGameId ? [form.baseGameId] : []}
        onValueChange={({ value }) =>
          setForm((prev) => ({ ...prev, baseGameId: value[0] ?? "" }))
        }
        loading={isLoading}
        error={isError ? "Error al cargar los juegos de mesa" : error}
      />
      <HStack justify="space-between" align="center">
        <Text fontSize="sm" color="fg.muted">
          ¿No encuentras el juego base?
        </Text>
        <CustomButton type="button" onClick={handleSubmit} disabled={disabled}>
          Crear juego base
        </CustomButton>
      </HStack>
    </VStack>
  );
}
