import {
  CustomAlert,
  CustomButton,
  CustomInput,
  TextSecondary,
  toaster,
} from "@/modules/core/components";
import { Flex, Heading, Link, VStack } from "@chakra-ui/react";
import type { UserLoginRequest } from "../types";
import { useState, type ChangeEvent } from "react";
import * as validation from "../validations/user.validations";
import { loginUser } from "../service/user.service";
import { isApiError } from "@/modules/core/utils/utils";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../core/context/useAuth";

export default function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const [form, setForm] = useState<UserLoginRequest>({
    username: "",
    password: "",
  });

  const [errors, setErrors] = useState<Record<string, string>>({
    username: "",
    password: "",
    general: "",
  });

  const resetErrors = () => {
    setErrors({
      username: "",
      password: "",
      general: "",
    });
  };

  const resetForm = () => {
    setForm({
      username: "",
      password: "",
    });
  };

  const [loadingLogin, setLoadingLogin] = useState(false);

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    resetErrors();
    setForm({
      ...form,
      [e.target.name]: e.target.value.trim(),
    });
  };

  const validateLoginForm = (form: UserLoginRequest) => {
    const newErrors: Record<string, string> = {
      username: validation.validateUsername(form.username),
      password: validation.validatePasswordAtLogin(form.password),
    };

    setErrors((prev) => ({ ...prev, ...newErrors }));
    return !Object.values(newErrors).some((error) => error !== "");
  };

  const handleSubmit = async () => {
    resetErrors();
    const isValid = validateLoginForm(form);
    if (!isValid) return;
    try {
      setLoadingLogin(true);
      const token = await loginUser(form);
      login(token);
      resetForm();
      toaster.create({
        title: "¡Bienvenido de nuevo!",
        description: "Has iniciado sesión exitosamente.",
        type: "success"

      });
      navigate("/");
    } catch (err) {
      console.error("Error al iniciar sesión:", err);
      if (isApiError(err))
        setErrors({ ...errors, general: "Error: " + err.message });
    } finally {
      setLoadingLogin(false);
    }
  };

  return (
    <Flex
      bg="background"
      borderRadius="xl"
      boxShadow="lg"
      p={6}
      direction="column"
      align="center"
      justify="flex-start"
      maxW="520px"
      minW="400px"
    >
      <VStack gap={4} w="100%">
        <Heading as="h1">Iniciar sesión</Heading>

        {errors.general && (
          <CustomAlert
            state="error"
            message={errors.general}
            closeable={false}
            maxW="400px"
          />
        )}

        <CustomInput
          label="Nombre de usuario"
          name="username"
          required={true}
          error={errors.username}
          onChange={handleChange}
        />

        <CustomInput
          label="Contraseña"
          name="password"
          required={true}
          error={errors.password}
          onChange={handleChange}
          password={true}
        />

        <TextSecondary>
          ¿Aún no tienes cuenta? Solicita tu registro{" "}
          <Link href="/registro" style={{ textDecoration: "underline" }}>
            aquí
          </Link>
          .
        </TextSecondary>

        <CustomButton onClick={handleSubmit} loading={loadingLogin}>
          Iniciar sesión
        </CustomButton>
      </VStack>
    </Flex>
  );
}
