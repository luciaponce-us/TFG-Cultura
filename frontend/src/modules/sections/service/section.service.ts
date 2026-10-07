import { authHeaders, fetchWithTimeout, handleResponse } from "@/modules/core/utils/utils";

import type { Section } from "../types";

import { SECTION_ROUTES } from "../routes";

export async function fetchAllSections(): Promise<Section[]> {
  const res = await fetchWithTimeout(SECTION_ROUTES.GET_ALL, {
    method: "GET",
  });

  return handleResponse<Section[]>(res);
}

export async function removeManagerFromSection(
  token: string,
  sectionId: string,
  managerUsername: string
): Promise<Section> {
  const res = await fetchWithTimeout(
    SECTION_ROUTES.REMOVE_MANAGER(sectionId, managerUsername),
    {
      method: "PUT",
      headers: authHeaders(token),
    },
  );

  return handleResponse<Section>(res);
}

export async function addManagerToSection(
  token: string,
  sectionId: string,
  managerUsername: string
): Promise<Section> {
  const res = await fetchWithTimeout(
    SECTION_ROUTES.ADD_MANAGER(sectionId, managerUsername),
    {
      method: "PUT",
      headers: authHeaders(token),
    },
  );

  return handleResponse<Section>(res);
}

export async function removeCollaboratorFromSection(
  token: string,
  sectionId: string,
  collaboratorUsername: string
): Promise<Section> {
  const res = await fetchWithTimeout(
    SECTION_ROUTES.REMOVE_COLLABORATOR(sectionId, collaboratorUsername),
    {
      method: "PUT",
      headers: authHeaders(token),
    },
  );

  return handleResponse<Section>(res);
}

export async function addCollaboratorToSection(
  token: string,
  sectionId: string,
  collaboratorUsername: string
): Promise<Section> {
  const res = await fetchWithTimeout(
    SECTION_ROUTES.ADD_COLLABORATOR(sectionId, collaboratorUsername),
    {
      method: "PUT",
      headers: authHeaders(token),
    },
  );

  return handleResponse<Section>(res);
}