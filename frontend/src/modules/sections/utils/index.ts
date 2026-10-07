import type { User } from "@/modules/users/types";
import {
  INITIAL_SECTION_FORM,
  type Section,
  type SectionRequest,
} from "../types";
import type { Paginated } from "@/modules/core/types";

export function toRequest(section: Section | undefined): SectionRequest {
  if (section) {
    return {
      name: section.name,
      managersUsernames: section.managers.map((manager) => manager.username),
      collaboratorsUsernames: section.collaborators.map(
        (collaborator) => collaborator.username,
      ),
    };
  } else {
    return INITIAL_SECTION_FORM;
  }
}

export function createUserOptions(paginatedUsers: Paginated<User> | undefined, selectedUsers: User[] | undefined): { value: string; label: string }[] {
  const users = paginatedUsers?.content || [];
  const selected = selectedUsers || [];

  const usersByUsername = new Map(
    [...selected, ...users].map((user) => [user.username, user]),
  );

  return [...usersByUsername.values()].map((user) => ({
    value: user.username,
    label: `${user.name} ${user.surname} (@${user.username})`,
  }));
}
