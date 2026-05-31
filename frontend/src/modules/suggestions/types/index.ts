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

export type SuggestionType = "CATALOG" | "EVENT" | "OTHER";
