import type { User } from "@/modules/users/types";
import { createContext } from "react";

export interface AuthContextType {
  token: string | null;
  user: User | null;
  login: (jwt: string) => void;
  logout: () => void;
}

export const AuthContext = createContext<AuthContextType | null>(null);
