import "./App.css";
import { Route, Routes } from "react-router-dom";
import { AuthProvider } from "./modules/core/context/AuthProvider";
import ProtectedRoute from "./modules/core/context/ProtectedRoute.tsx";
import type { Role } from "./modules/users/types/index.ts";
import Layout from "./modules/core/Layout";
import { Toaster } from "./modules/core/components";
import { AdminPanel, Home, NotFound, PrivacyPolicyPage, RulesPage, TermsOfUsePage } from "./modules/core/pages";
import {
  RegistrationPage,
  LoginPage,
  UsersAdminPage,
  EditUserPage,
  ProfilePage,
  EditProfilePage,
} from "./modules/users/pages";
import { SuggestionsPage } from "@/modules/suggestions/pages";

export default function App() {
  const MANAGEMENT_ROLES: Role[] = [
    "COORDINADOR",
    "SECRETARIO",
    "ENCARGADO",
    "COLABORADOR",
  ];

  return (
    <>
      <Toaster />
      <AuthProvider>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<Home />} />
            <Route path="/terminos-de-uso" element={<TermsOfUsePage />} />
            <Route path="/politica-de-privacidad" element={<PrivacyPolicyPage />} />
            <Route path="/normas" element={<RulesPage />} />
            <Route path="/registro" element={<RegistrationPage />} />
            <Route path="/iniciar-sesion" element={<LoginPage />} />
            <Route path="/sugerencias" element={<SuggestionsPage />} />
            <Route
              path="/mis-sugerencias"
              element={<SuggestionsPage mySuggestions />}
            />
            <Route path="/perfil" element={<ProfilePage />} />
            <Route path="/perfil/editar" element={<EditProfilePage />} />
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
            <Route path="/no-encontrado" element={<NotFound />} />
            <Route path="*" element={<NotFound />} />
          </Route>
        </Routes>
      </AuthProvider>
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
