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

  useEffect(() => {
    if (!token) return;
    const loadUser = async () => {
      const res = await getMyProfile(token);
      setUser(res);
    };
    try {
      loadUser();
    } catch (error) {
      console.error("Error loading user profile:", error);
    }
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

  const value = useMemo(
    () => ({
      token,
      user,
      login,
      logout,
    }),
    [token, user],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}
