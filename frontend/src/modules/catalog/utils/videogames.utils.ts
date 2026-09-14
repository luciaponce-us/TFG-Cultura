import type { Platform } from "../types/videogame";

export function parsePlatform(platform: Platform): string {
  switch (platform) {
    case "N_SWITCH_2":
      return "Nintendo Switch 2";
    case "N_SWITCH":
      return "Nintendo Switch";
    case "QUEST":
      return "Oculus Quest";
    case "PS5":
      return "PlayStation 5";
    case "PS4":
      return "PlayStation 4";
    case "PS3":
      return "PlayStation 3";
    case "PS2":
      return "PlayStation 2";
    case "PS1":
      return "PlayStation 1";
    case "XBOX360":
      return "Xbox 360";
    case "XBOX_SERIES":
      return "Xbox Series";
    case "WII":
      return "Nintendo Wii";
    case "PS_VITA":
      return "PlayStation Vita";
    case "PSP":
      return "PlayStation Portable";
    case "NINTENDO_3DS":
      return "Nintendo 3DS";
    case "NINTENDO_DS":
      return "Nintendo DS";
    case "PC":
      return "PC";
    default:
      return platform;
  }
}
