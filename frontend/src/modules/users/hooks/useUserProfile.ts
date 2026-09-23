import { useQuery } from "@tanstack/react-query";
import { getMyProfile } from "../service/user.service";
import { isApiError } from "@/modules/core/utils/utils";

type UseUserProfileOptions = {
  token: string | null;
  onUnauthorized?: () => void;
};

export function useUserProfile({
  token,
  onUnauthorized,
}: UseUserProfileOptions) {
  return useQuery({
    queryKey: ["userProfile"],
    queryFn: async () => {
      try {
        return await getMyProfile(token!);
      } catch (error) {
        if (isApiError(error) && error.status === 401) {
          onUnauthorized?.();
        }

        console.error("Error al obtener el perfil del usuario:", error);
      }
    },
    enabled: !!token,
    staleTime: 1000 * 60 * 5, // 5 minutos (opcional)
  });
}
