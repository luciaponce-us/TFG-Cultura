import { Route, Routes } from "react-router-dom";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

import { AuthProvider } from "./modules/core/context/AuthProvider";
import ProtectedRoute from "./modules/core/context/ProtectedRoute.tsx";

import Layout from "./modules/core/Layout";
import { Toaster } from "./modules/core/components";

import {
  AdminPanel,
  Home,
  NotFound,
  PrivacyPolicyPage,
  RulesPage,
  TermsOfUsePage,
} from "./modules/core/pages";

import {
  RegistrationPage,
  LoginPage,
  UsersAdminPage,
  EditUserPage,
  ProfilePage,
  EditProfilePage,
} from "./modules/users/pages";

import { SuggestionsPage } from "@/modules/suggestions/pages";

import {
  BooksPage,
  MangasAndComicsPage,
  MoviesPage,
  SeriesPage,
  BoardGamesPage,
  CatalogPage,
  RolSagasPage,
  RolSagaPage,
  VideoGamesPage,
  SagasPage,
  BookPage,
  BoardGamePage,
} from "@/modules/catalog/pages";

import { CategoriesPage } from "@/modules/categories/pages";

import { type Role, MANAGEMENT_ROLES } from "./modules/users/types";

const queryClient = new QueryClient();

export default function App() {
  return (
    <>
      <Toaster />
      <QueryClientProvider client={queryClient}>
        <AuthProvider>
          <Routes>
            <Route path="/" element={<Layout />}>
              <Route index element={<Home />} />
              <Route path="/terminos-de-uso" element={<TermsOfUsePage />} />
              <Route
                path="/politica-de-privacidad"
                element={<PrivacyPolicyPage />}
              />
              <Route path="/normas" element={<RulesPage />} />
              <Route path="/registro" element={<RegistrationPage />} />
              <Route path="/iniciar-sesion" element={<LoginPage />} />
              <Route path="/sugerencias" element={<SuggestionsPage />} />
              <Route
                path="/mis-sugerencias"
                element={<SuggestionsPage mySuggestions />}
              />

              {/* CATÁLOGO */}
              <Route path="/catalogo" element={<CatalogPage />} />
              <Route path="/catalogo/libros" element={<BooksPage />} />
              <Route path="/catalogo/libros/:bookId" element={<BookPage />} />
              <Route
                path="/catalogo/mangas-y-comics"
                element={<MangasAndComicsPage />}
              />
              <Route path="/catalogo/peliculas" element={<MoviesPage />} />
              <Route path="/catalogo/series" element={<SeriesPage />} />
              <Route
                path="/catalogo/juegos-de-mesa"
                element={<BoardGamesPage />}
              />
              <Route
                path="/catalogo/juegos-de-mesa/:boardGameId"
                element={<BoardGamePage />}
              />
              <Route path="/catalogo/rol" element={<RolSagasPage />} />
              <Route path="/catalogo/rol/:sagaId" element={<RolSagaPage />} />
              <Route
                path="/catalogo/videojuegos"
                element={<VideoGamesPage />}
              />
              {/* PERFIL */}
              <Route path="/perfil" element={<ProfilePage />} />
              <Route path="/perfil/editar" element={<EditProfilePage />} />
              {/* ADMIN */}
              <Route
                path="/admin"
                element={withRoleProtection(AdminPanel, MANAGEMENT_ROLES)}
              />
              <Route
                path="/admin/usuarios"
                element={withRoleProtection(UsersAdminPage, MANAGEMENT_ROLES)}
              />
              <Route
                path="/admin/usuarios/:username"
                element={withRoleProtection(EditUserPage, MANAGEMENT_ROLES)}
              />
              {/* CATEGORÍAS */}
              <Route
                path="/admin/categorias"
                element={withRoleProtection(CategoriesPage, MANAGEMENT_ROLES)}
              />
              {/* SAGAS */}
              <Route
                path="/admin/sagas"
                element={withRoleProtection(SagasPage, MANAGEMENT_ROLES)}
              />
              <Route path="/no-encontrado" element={<NotFound />} />
              <Route path="*" element={<NotFound />} />
            </Route>
          </Routes>
        </AuthProvider>
      </QueryClientProvider>
    </>
  );
}

function withRoleProtection(
  Component: React.ComponentType,
  allowedRoles: Role[],
) {
  return (
    <ProtectedRoute allowedRoles={allowedRoles}>
      <Component />
    </ProtectedRoute>
  );
}
