import { useState } from "react";
import { HStack, Text, VStack } from "@chakra-ui/react";
import {
  CustomButton,
  CustomSearchBar,
  CustomSelect,
} from "@/modules/core/components";
import { useBoardGames } from "../hooks";
import type { BoardGameRequest } from "../types/boardgame";

interface BaseGameSelectProps {
  readonly form: BoardGameRequest;
  readonly setForm: React.Dispatch<React.SetStateAction<BoardGameRequest>>;
  readonly error?: string;
  readonly onCreateBaseGame: () => void;
}

export function BaseGameSelect({
  form,
  setForm,
  error,
  onCreateBaseGame,
}: BaseGameSelectProps) {
  const [search, setSearch] = useState("");
  const { data, isLoading, isError } = useBoardGames(0, {
    nameContains: search,
    categories: [],
  });

  const baseGameOptions =
    data?.content
      .filter(
        (boardGame) => !boardGame.isExpansion && boardGame.baseGame === null,
      )
      .map((boardGame) => ({ value: boardGame.id, label: boardGame.name })) ??
    [];

  return (
    <VStack align="stretch" gap={3} w="100%">
      <Text fontWeight="medium">Juego base</Text>
      <CustomSearchBar
        value={search}
        onChange={(event) => setSearch(event.currentTarget.value)}
        placeholder="Buscar juego base..."
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
        <CustomButton type="button" onClick={onCreateBaseGame}>
          Crear juego base
        </CustomButton>
      </HStack>
    </VStack>
  );
}
