import type { Item, ItemErrors, ItemRequest } from "./";
import { INITIAL_ITEM, INITIAL_ITEM_ERRORS } from "./";

export interface VideoGame extends Item {
    platform: Platform;
	releaseDate: string;
	trailerUrl: string;
}

export const PLATFORMS = ["N_SWITCH_2", "N_SWITCH", "QUEST", "PS5", "PS4", "PS3", "PS2", "PS1", "XBOX360", "XBOX_SERIES", "WII", "PS_VITA", "PSP", "NINTENDO_3DS", "NINTENDO_DS", "PC"] as const;

export type Platform = (typeof PLATFORMS)[number];

export const PLATFORMS_OPTIONS = [
    { value: "N_SWITCH_2", label: "Nintendo Switch 2" },
    { value: "N_SWITCH", label: "Nintendo Switch" },
    { value: "QUEST", label: "Oculus Quest" },
    { value: "PS5", label: "PlayStation 5" },
    { value: "PS4", label: "PlayStation 4" },
    { value: "PS3", label: "PlayStation 3" },
    { value: "PS2", label: "PlayStation 2" },
    { value: "PS1", label: "PlayStation 1" },
    { value: "XBOX360", label: "Xbox 360" },
    { value: "XBOX_SERIES", label: "Xbox Series" },
    { value: "WII", label: "Nintendo Wii" },
    { value: "PS_VITA", label: "PlayStation Vita" },
    { value: "PSP", label: "PlayStation Portable" },
    { value: "NINTENDO_3DS", label: "Nintendo 3DS" },
    { value: "NINTENDO_DS", label: "Nintendo DS" },
    { value: "PC", label: "PC" },
];

export interface VideoGameRequest extends ItemRequest {
    platform: Platform;
    releaseDate: string;
    trailerUrl: string;
}

export const INITIAL_VIDEO_GAME: VideoGameRequest = {
    ...INITIAL_ITEM,
    platform: "PC",
    releaseDate: "",
    trailerUrl: "",
};

export interface VideoGameErrors extends ItemErrors {
    platform?: string;
    releaseDate?: string;
    trailerUrl?: string;
}

export const INITIAL_VIDEO_GAME_ERRORS: VideoGameErrors = {
    ...INITIAL_ITEM_ERRORS,
    platform: "",
    releaseDate: "",
    trailerUrl: "",
};