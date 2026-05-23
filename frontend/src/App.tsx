import "./App.css";
import { Route, Routes } from "react-router-dom";
import Layout from "./modules/core/Layout";
import { AuthProvider } from "./modules/core/context/AuthProvider";
import { AdminPanel, Home, NotFound } from "./modules/core/pages";
import { RegistrationPage, LoginPage } from "./modules/users/pages";
import { Toaster } from "./modules/core/components";

export default function App() {
  return (
    <>
      <Toaster />
      <AuthProvider>
        <Routes>
          <Route path="/" element={<Layout />}>
            <Route index element={<Home />} />
            <Route path="/registro" element={<RegistrationPage />} />
            <Route path="/iniciar-sesion" element={<LoginPage />} />
            <Route path="/admin" element={<AdminPanel />} />
            <Route path="*" element={<NotFound />} />
          </Route>
        </Routes>
      </AuthProvider>
    </>
  );
}
