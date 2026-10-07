import type { User } from "@/modules/users/types";

export interface Section {
  id: string;
  name: string;
  managers: User[];
  collaborators: User[];
}

export interface SectionReference {
  id: string;
  name: string;
}

export interface SectionRequest {
  name: string;
  managersUsernames: string[];
  collaboratorsUsernames: string[];
}

export const INITIAL_SECTION_FORM: SectionRequest = {
  name: "",
  managersUsernames: [],
  collaboratorsUsernames: [],
};

export interface SectionErrors {
  name?: string;
  managersUsernames?: string;
  collaboratorsUsernames?: string;
}

export const INITIAL_SECTION_ERRORS: SectionErrors = {
  name: "",
  managersUsernames: "",
  collaboratorsUsernames: "",
};
