import { HStack, Link, Text } from "@chakra-ui/react";
import { useBoardGame } from "../hooks";
import { CreateBoardGameDialog } from "../components";
import { useParams } from "react-router-dom";
import { ITEM_TYPES } from "../types";
import { ItemPage } from "./ItemPage";
import { PLACEHOLDER } from "@/modules/core/utils/utils";
import type { BoardGame } from "../types/boardgame";
import {
  IconChartPie4,
  IconClockHour3,
  IconModelAi,
  IconUsers,
} from "@tabler/icons-react";
import {
  parseBoardGameTypes,
  parseComplexity,
} from "../utils/boardgames.utils";
import { COLORS } from "@/styles/theme";

export function BoardGamePage() {
  const { boardGameId } = useParams<{ boardGameId: string }>();
  const { data: boardGame, isLoading, isError } = useBoardGame(boardGameId);

  return (
    <ItemPage
      item={boardGame}
      isLoading={isLoading}
      isError={isError}
      itemId={boardGameId ?? ""}
      itemType={ITEM_TYPES.BOARD_GAME}
      placeholderImage={PLACEHOLDER.BOARDGAME}
      errorMessage="Ha ocurrido un error al cargar el juego de mesa. Vuelve a intentarlo más tarde."
      CreateItemDialogComponent={CreateBoardGameDialog}
      subtitle={Subtitle({ boardGame })}
      extraInfo={ExtraInfo({ boardGame })}
    />
  );
}

function Subtitle({
  boardGame,
}: {
  boardGame: BoardGame | undefined;
}): React.ReactNode | undefined {
  if (!boardGame || !boardGame.isExpansion || !boardGame.baseGame)
    return undefined;
  return (
    <HStack gap={1}>
      <Text>Expansión de</Text>
      <Link
        href={`/catalogo/juegos-de-mesa/${boardGame.baseGame.id}`}
        color="principal.500"
      >
        {boardGame.baseGame.name}
      </Link>
    </HStack>
  );
}

function ExtraInfo({
  boardGame,
}: {
  boardGame: BoardGame | undefined;
}): React.ReactNode | undefined {
  if (!boardGame) return undefined;

  return (
    <>
      <HStack>
        <IconUsers color={COLORS.TEXT_HEADER} />
        <Text fontWeight="bold">Jugadores:</Text>
        <Text>{`${boardGame.minPlayers}-${boardGame.maxPlayers}`}</Text>
      </HStack>
      <HStack>
        <IconClockHour3 color={COLORS.TEXT_HEADER} />
        <Text fontWeight="bold">Tiempo de juego:</Text>
        <Text>{`${boardGame.playTime} min`}</Text>
      </HStack>
      <HStack>
        <IconModelAi color={COLORS.TEXT_HEADER} />
        <Text fontWeight="bold">Complejidad:</Text>
        <Text>{parseComplexity(boardGame.complexity)}</Text>
      </HStack>
      <HStack>
        <IconChartPie4 color={COLORS.TEXT_HEADER} />
        <Text fontWeight="bold">Tipos:</Text>
        <Text>{parseBoardGameTypes(boardGame.types)}</Text>
      </HStack>
    </>
  );
}
