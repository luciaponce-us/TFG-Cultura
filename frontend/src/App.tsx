import "./App.css";
import { Route, Routes } from "react-router-dom";
import Layout from "./modules/core/Layout";
import { AuthProvider } from "./modules/core/context/AuthProvider";
import ProtectedRoute from "./modules/core/context/ProtectedRoute.tsx";
import { AdminPanel, Home, NotFound } from "./modules/core/pages";
import { RegistrationPage, LoginPage } from "./modules/users/pages";
import { Toaster } from "./modules/core/components";
import type { Role } from "./modules/users/types/index.ts";

export default function App() {
  const MANAGEMENT_ROLES: Role[] = [
    "COORDINADOR",
    "SECRETARIO",
    "ENCARGADO",
    "COLABORADOR",
  ];
  const ADMIN_ROLES: Role[] = ["COORDINADOR", "SECRETARIO"];

  return (
    <>
      <Toaster />
      <AuthProvider>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<Home />} />
            <Route path="/registro" element={<RegistrationPage />} />
            <Route path="/iniciar-sesion" element={<LoginPage />} />
            <Route
              path="/admin"
              element={withRoleProtection(AdminPanel, MANAGEMENT_ROLES)}
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
