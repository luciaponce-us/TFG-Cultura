import { useCallback, useMemo, useState, type ReactNode } from "react";
import { useQueryClient } from "@tanstack/react-query";

import { AuthContext } from "./AuthContext";

import { useUserProfile } from "@/modules/users/hooks";
import { MANAGEMENT_ROLES } from "@/modules/users/types";

import type { User } from "@/modules/users/types";

interface AuthProviderProps {
  readonly children: ReactNode;
}

export function AuthProvider({ children }: AuthProviderProps) {
  const queryClient = useQueryClient();

  const [token, setToken] = useState<string | null>(() =>
    localStorage.getItem("token"),
  );

  const login = useCallback((jwt: string) => {
    localStorage.setItem("token", jwt);
    setToken(jwt);
  }, []);

  const logout = useCallback(() => {
    localStorage.removeItem("token");
    setToken(null);

    queryClient.removeQueries({
      queryKey: ["userProfile"],
    });
  }, [queryClient]);

  const updateUser = useCallback(
    (nextUser: User | null) => {
      queryClient.setQueryData(["userProfile"], nextUser);
    },
    [queryClient],
  );

  const { data: user } = useUserProfile({
    token,
    onUnauthorized: logout,
  });

  const isAdmin = user ? MANAGEMENT_ROLES.includes(user.role) : false;

  const value = useMemo(
    () => ({
      token,
      user,
      login,
      logout,
      setUser: updateUser,
      isAdmin,
    }),
    [token, user, login, logout, updateUser, isAdmin],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
