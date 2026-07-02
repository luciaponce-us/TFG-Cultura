import {
  CustomAlert,
  CustomButton,
  CustomInput,
  TextSecondary,
  toaster,
} from "@/modules/core/components";
import { Flex, Heading, Link, VStack } from "@chakra-ui/react";
import type { UserLoginRequest } from "../types";
import { useEffect, useState } from "react";
import * as validation from "../validations/user.validations";
import { loginUser } from "../service/user.service";
import { handleChange, isApiError } from "@/modules/core/utils/utils";
import { useNavigate } from "react-router-dom";
import { useAuth } from "../../core/context/useAuth";

export function LoginPage() {
  const navigate = useNavigate();
  const { login } = useAuth();

  const defaultForm: UserLoginRequest = {
    username: "",
    password: "",
  };

  const defaultErrors: Record<string, string> = {
    username: "",
    password: "",
    general: "",
  };

  const [form, setForm] = useState<UserLoginRequest>(defaultForm);

  const [errors, setErrors] = useState<Record<string, string>>(defaultErrors);

  function resetErrors() {
    setErrors(defaultErrors);
  }

  function resetForm() {
    setForm(defaultForm);
  }

  useEffect(() => {
    window.scrollTo({ top: 0, behavior: "smooth" });
  }, []);

  const [loadingLogin, setLoadingLogin] = useState(false);

  function validateLoginForm(form: UserLoginRequest): boolean {
    const newErrors: Record<string, string> = {
      username: validation.validateUsername(form.username),
      password: validation.validatePasswordAtLogin(form.password),
    };

    setErrors((prev) => ({ ...prev, ...newErrors }));
    return !Object.values(newErrors).some((error) => error !== "");
  }

  async function handleSubmit() {
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
        type: "success",
      });
      void navigate("/");
    } catch (err) {
      console.error("Error al iniciar sesión:", err);
      if (isApiError(err))
        setErrors({
          ...errors,
          general:
            "Error al iniciar sesión. Por favor, intentálo de nuevo más tarde.",
        });
    } finally {
      setLoadingLogin(false);
    }
  }

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
      <VStack
        as="form"
        onSubmit={(e) => {
          e.preventDefault();
          void handleSubmit();
        }}
        gap={4}
        w="100%"
      >
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
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
        />

        <CustomInput
          label="Contraseña"
          name="password"
          required={true}
          error={errors.password}
          onChange={(e) => handleChange(e, form, setErrors, setForm)}
          password={true}
        />

        <TextSecondary>
          ¿Aún no tienes cuenta? Solicita tu registro{" "}
          <Link href="/registro" style={{ textDecoration: "underline" }}>
            aquí
          </Link>
          .
        </TextSecondary>

        <CustomButton
          onClick={() => void handleSubmit()}
          loading={loadingLogin}
          type="submit"
        >
          Iniciar sesión
        </CustomButton>
      </VStack>
    </Flex>
  );
}
