import type { Item, ItemErrors, ItemRequest } from "./";
import { INITIAL_ITEM, INITIAL_ITEM_ERRORS } from "./";
import type { Format } from "./movie";

export interface Season {
	seasonNumber: number;
	seasonPart?: number;
	trailerUrl?: string;
}

export interface Series extends Item {
	format: Format;
	numberOfDiscs: number;
	releaseDate: string;
	numberOfSeasons: number;
	status: SeriesStatus;
	seasons: Season[];
}

export const SERIES_STATUSES = [
	"ONGOING",
	"FINISHED",
	"CANCELLED",
	"HIATUS",
] as const;

export type SeriesStatus = (typeof SERIES_STATUSES)[number];

export const SERIES_STATUSES_OPTIONS = [
	{ value: "ONGOING", label: "En emisión" },
	{ value: "FINISHED", label: "Finalizada" },
	{ value: "CANCELLED", label: "Cancelada" },
	{ value: "HIATUS", label: "En pausa" },
];

export interface SeriesRequest extends ItemRequest {
	format: Format;
	numberOfDiscs: number;
	releaseDate: string;
	numberOfSeasons: number;
	status: SeriesStatus;
	seasons: Season[];
}

export const INITIAL_SERIES: SeriesRequest = {
	...INITIAL_ITEM,
	format: "DVD",
	numberOfDiscs: 1,
	releaseDate: "",
	numberOfSeasons: 1,
	status: "ONGOING",
	seasons: [
		{
			seasonNumber: 1,
			seasonPart: 0,
			trailerUrl: "",
		},
	],
};

export interface SeriesErrors extends ItemErrors {
	format?: string;
	numberOfDiscs?: string;
	releaseDate?: string;
	numberOfSeasons?: string;
	status?: string;
	seasons?: string;
}

export const INITIAL_SERIES_ERRORS: SeriesErrors = {
	...INITIAL_ITEM_ERRORS,
	format: "",
	numberOfDiscs: "",
	releaseDate: "",
	numberOfSeasons: "",
	status: "",
	seasons: "",
};
