import type { User } from "@/modules/users/types";
import { createContext } from "react";

export interface AuthContextType {
  token: string | null;
  user: User | null;
  isLoading: boolean;
  login: (jwt: string) => void;
  logout: () => void;
  setUser: (user: User | null) => void;
  isAdmin: boolean;
}

export const AuthContext = createContext<AuthContextType | null>(null);
