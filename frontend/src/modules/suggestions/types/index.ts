import type { User } from "@/modules/users/types";

export interface Suggestion {
  id: string;
  title: string;
  description: string;
  type: SuggestionType;
  author: User;
  someSupportersAvatars: string[];
  supporters: User[];
  totalSupporters: number;
  createdAt: Date;
}

export interface FiltersGetAllSuggestions {
  type?: SuggestionType;
  text: string;
  orderByCreationDate: boolean;
  supportedByAdmins: boolean;
}

export const SUGGESTION_TYPES = ["CATALOG", "EVENT", "OTHER"] as const;

export type SuggestionType = typeof SUGGESTION_TYPES[number];

export interface SuggestionCreateRequest {
  title: string;
  description?: string;
  type?: SuggestionType;
}
