import type { Role, User } from "@/modules/users/types";
import type { ReactNode } from "react";
import { IconUser, IconBubble, IconSettings } from "@tabler/icons-react";

export type NavLink = {
  icon: ReactNode | null;
  title: string;
  href: string;
};

export const ADMIN_ROLES: Role[] = [
  "COORDINADOR",
  "SECRETARIO",
  "ENCARGADO",
  "COLABORADOR",
];

export const MAIN_MENU_LINKS: NavLink[] = [
  { icon: null, title: "Inicio", href: "/" },
  { icon: null, title: "Sugerencias", href: "/sugerencias" },
];

export const CATALOG_SUBMENU_LINKS: NavLink[] = [
  { icon: null, title: "Libros", href: "/catalogo/libros" },
  { icon: null, title: "Manga y cómics", href: "/catalogo/manga-y-comics" },
  { icon: null, title: "Películas", href: "/catalogo/peliculas" },
  { icon: null, title: "Series", href: "/catalogo/series" },
  { icon: null, title: "Juegos de mesa", href: "/catalogo/juegos-de-mesa" },
  { icon: null, title: "Rol", href: "/catalogo/rol" },
  { icon: null, title: "Videojuegos", href: "/catalogo/videojuegos" },
];

// Si no usamos tsx no podemos colocar así los iconos
export const LOGGED_USER_LINKS: NavLink[] = [
  { icon: <IconUser />, title: "Mi perfil", href: "/perfil" },
  { icon: <IconBubble />, title: "Mis sugerencias", href: "/mis-sugerencias" },
];

export const ADMIN_LINKS: NavLink[] = [
  {
    icon: <IconSettings />,
    title: "Panel de administración",
    href: "/admin",
  },
];

export const NOT_LOGGED_USER_LINKS: NavLink[] = [
  { icon: null, title: "Iniciar sesión", href: "/iniciar-sesion" },
  { icon: null, title: "Registrarse", href: "/registro" },
];

export function getUserLinks(user: User | null | undefined): NavLink[] {
  if (!user) return NOT_LOGGED_USER_LINKS;

  const base = [...LOGGED_USER_LINKS];

  const isAdmin = ADMIN_ROLES.includes(user.role);

  if (isAdmin) {
    return [...base, ...ADMIN_LINKS];
  }

  return base;
}
