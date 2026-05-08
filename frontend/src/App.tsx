import "./App.css";
import { Route, Routes } from "react-router-dom";
import Home from "./modules/core/pages/HomePage";
import Layout from "./modules/core/Layout";
import { AuthProvider } from "./modules/core/context/AuthProvider";

export default function App() {
  return (
    <AuthProvider>
      <Routes>
        <Route path="/" element={<Layout />}>
          <Route index element={<Home />} />
        </Route>
      </Routes>
    </AuthProvider>
  );
}
