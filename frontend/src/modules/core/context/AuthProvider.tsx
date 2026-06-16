import { useEffect, useMemo, useState } from "react";
import type { ReactNode } from "react";
import { AuthContext } from "./AuthContext";
import type { User } from "@/modules/users/types";
import { getMyProfile } from "@/modules/users/service/user.service";
import { MANAGEMENT_ROLES } from "@/modules/users/types";

interface AuthProviderProps {
  readonly children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const [token, setToken] = useState<string | null>(
    localStorage.getItem("token"),
  );
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);

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

  const isAdmin = user ? MANAGEMENT_ROLES.includes(user.role) : false;

  useEffect(() => {
    async function loadUser() {
      try {
        if (!token) {
          logout();
          return;
        }
        const res = await getMyProfile(token);
        console.log("Perfil del usuario cargado:", res);
        setUser(res);
      } catch (error) {
        console.error("Error loading user profile:", error);
        logout();
      } finally {
        setIsLoading(false);
      }
    }

    loadUser();
  }, [token]);

  const value = useMemo(
    () => ({
      token,
      user,
      isLoading,
      login,
      logout,
      setUser: updateUser,
      isAdmin,
    }),
    [token, user, isLoading, isAdmin],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
