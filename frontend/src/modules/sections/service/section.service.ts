import { authHeaders, fetchWithTimeout, handleResponse, jsonHeaders } from "@/modules/core/utils/utils";

import type { Section, SectionRequest } from "../types";

import { SECTION_ROUTES } from "../routes";

export async function createSection(
  token: string,
  sectionRequest: SectionRequest
): Promise<Section> {
  const res = await fetchWithTimeout(SECTION_ROUTES.GET_ALL, {
    method: "POST",
    headers: {...authHeaders(token), ...jsonHeaders},
    body: JSON.stringify(sectionRequest),
  });

  return handleResponse<Section>(res);
}

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

export async function deleteSection(token: string, sectionId: string): Promise<void> {
  const res = await fetchWithTimeout(
    SECTION_ROUTES.GET_BY_ID(sectionId),
    {
      method: "DELETE",
      headers: authHeaders(token),
    },
  );

  return handleResponse<void>(res);
}