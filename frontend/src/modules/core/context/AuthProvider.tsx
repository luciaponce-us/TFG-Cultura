import { useEffect, useMemo, useState } from "react";
import type { ReactNode } from "react";
import { AuthContext } from "./AuthContext";
import type { User } from "@/modules/users/types";
import { getMyProfile } from "@/modules/users/service/user.service";

interface AuthProviderProps {
  readonly children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [token, setToken] = useState<string | null>(
    localStorage.getItem("token"),
  );
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

  useEffect(() => {
    console.log("Token actual:", token);
    if (!token) {
      setIsLoading(false);
      return;
    }

    const loadUser = async () => {
      try {
        const res = await getMyProfile(token);
        console.log("Perfil del usuario cargado:", res);
        setUser(res);
      } catch (error) {
        console.error("Error loading user profile:", error);
        // Si hay error al cargar el perfil, limpiar el token inválido
        localStorage.removeItem("token");
        setToken(null);
        setUser(null);
      } finally {
        setIsLoading(false);
      }
    };

    loadUser();
  }, [token]);

  const login = (jwt: string) => {
    localStorage.setItem("token", jwt);
    setToken(jwt);
  };

  const logout = () => {
    localStorage.removeItem("token");
    setToken(null);
    setUser(null);
  };

  const updateUser = (nextUser: User | null) => {
    setUser(nextUser);
  };

  const value = useMemo(
    () => ({
      token,
      user,
      isLoading,
      login,
      logout,
      setUser: updateUser,
    }),
    [token, user, isLoading],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
