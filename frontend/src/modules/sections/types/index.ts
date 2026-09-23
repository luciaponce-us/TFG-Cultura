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
