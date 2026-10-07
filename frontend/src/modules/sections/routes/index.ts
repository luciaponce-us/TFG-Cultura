import { API_BASE_URL } from "@/modules/core/utils/utils";

export const SECTION_ROUTES = {
  GET_ALL: `${API_BASE_URL}/api/sections`,
  GET_BY_ID: (id: string) => `${SECTION_ROUTES.GET_ALL}/${id}`,
  REMOVE_MANAGER: (sectionId: string, managerUsername: string) =>
    `${SECTION_ROUTES.GET_BY_ID(sectionId)}/managers/${managerUsername}/remove`,
  REMOVE_COLLABORATOR: (sectionId: string, collaboratorUsername: string) =>
    `${SECTION_ROUTES.GET_BY_ID(sectionId)}/collaborators/${collaboratorUsername}/remove`,
};
