import type { Item, ItemRequest, ItemErrors } from "./";
import type { Saga } from "./saga";
import { INITIAL_ITEM, INITIAL_ITEM_ERRORS } from "./";

export interface Movie extends Item {
	format: Format;
	numberOfDiscs: number;
	releaseDate: string;
	trailerUrl: string;
	saga: Saga;
}

export const FORMATS = ["DVD", "BLURAY", "UHD_4K"] as const;

export type Format = (typeof FORMATS)[number];

export const FORMATS_OPTIONS = [
	{ value: "DVD", label: "DVD" },
	{ value: "BLURAY", label: "Blu-ray" },
	{ value: "UHD_4K", label: "4K" },
];

export interface MovieRequest extends ItemRequest {
	format: Format;
	numberOfDiscs: number;
	releaseDate?: string;
	trailerUrl?: string;
	sagaName?: string;
}

export const INITIAL_MOVIE: MovieRequest = {
	...INITIAL_ITEM,
	format: "DVD",
	numberOfDiscs: 1,
	releaseDate: "",
	trailerUrl: "",
	sagaName: "",
};

export interface MovieErrors extends ItemErrors {
	format?: string;
	numberOfDiscs?: string;
	releaseDate?: string;
	trailerUrl?: string;
	sagaName?: string;
}

export const INITIAL_MOVIE_ERRORS: MovieErrors = {
	...INITIAL_ITEM_ERRORS,
	format: "",
	numberOfDiscs: "",
	releaseDate: "",
	trailerUrl: "",
	sagaName: "",
};
