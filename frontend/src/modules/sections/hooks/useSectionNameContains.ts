import type { Section } from "../types";
import { useSections } from "./useSections";

export function useSectionNameContains(nameContains: string): Section {
  const { data: allSections } = useSections();
  return allSections?.find((section) =>
    section.name.toLowerCase().includes(nameContains.toLowerCase()),
  ) as Section;
}
