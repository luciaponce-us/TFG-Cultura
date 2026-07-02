import {
  VStack,
  Heading,
  Flex,
  Text,
  HStack,
  Checkbox,
  Link,
  Field,
} from "@chakra-ui/react";
import { useState } from "react";
import { useNavigate } from "react-router-dom";
import { registerUser } from "../service/user.service";
import type { UserRegisterRequest } from "../types";
import { handleChange, isApiError } from "@/modules/core/utils/utils";
import {
  CustomAlert,
  CustomButton,
  TextSecondary,
  CustomInput,
  UploadBox,
  toaster,
} from "@/modules/core/components";
import { IconArrowNarrowLeft, IconArrowNarrowRight } from "@tabler/icons-react";
import * as validation from "../validations/user.validations";

type RegistrationForm = UserRegisterRequest & {
  confirmPassword: string;
};

export function RegistrationPage() {
  const defaultForm: RegistrationForm = {
    username: "",
    password: "",
    name: "",
    surname: "",
    dni: "",
    phone: "",
    email: "",
    confirmPassword: "",
  };

  const defaultErrors: Record<string, string> = {
    username: "",
    password: "",
    name: "",
    surname: "",
    dni: "",
    phone: "",
    email: "",
    general: "",
    termsAccepted: "",
    rulesAccepted: "",
  };

  const navigate = useNavigate();

  const [form, setForm] = useState<RegistrationForm>(defaultForm);
  const [errors, setErrors] = useState<Record<string, string>>(defaultErrors);
  const [avatar, setAvatar] = useState<File | null>(null);
  const [paymentReceipt, setPaymentReceipt] = useState<File | null>(null);
  const [termsAccepted, setTermsAccepted] = useState(false);
  const [rulesAccepted, setRulesAccepted] = useState(false);

  const [step, setStep] = useState(1);
  const [loadingRegister, setLoadingRegister] = useState(false);

  async function handleSubmit() {
    setErrors(defaultErrors);

    const isValidStep1 = validateStep1(form) && paymentReceipt;
    const isValidStep2 = validateStep2(form);
    const isValid = isValidStep1 && isValidStep2;
    if (!isValid) {
      toaster.create({
        title: "Error",
        description: "Por favor corrige los errores en el formulario.",
        type: "error",
      });
      return;
    }
    try {
      setLoadingRegister(true);
      await registerUser(form, paymentReceipt, avatar || undefined);
      setStep(3);
      setForm(defaultForm);
    } catch (err) {
      console.error("Error al registrar usuario:", err);
      if (isApiError(err)) {
        if (err.message.includes("DNI")) {
          setErrors({
            ...errors,
            dni: "El DNI ya está registrado",
            general: "El DNI ya está registrado. Vuelve atrás para corregirlo.",
          });
        } else {
          setErrors({
            ...errors,
            general:
              "Ha ocurrido un error durante el registro. Inténtalo de nuevo más tarde.",
          });
        }
      }
    } finally {
      setLoadingRegister(false);
    }
  }

  function validateStep1(form: RegistrationForm): boolean {
    const newErrors = {
      name: validation.validateName(form.name),
      surname: validation.validateSurname(form.surname),
      dni: validation.validateDni(form.dni),
      general: paymentReceipt ? "" : "La carta de pago es obligatoria",
      termsAccepted: termsAccepted
        ? ""
        : "Debes aceptar los términos y condiciones",
      rulesAccepted: rulesAccepted ? "" : "Debes aceptar las normas de uso",
    };

    setErrors((prev) => ({ ...prev, ...newErrors }));
    const isValid = !Object.values(newErrors).some((error) => error !== "");
    if (!isValid) {
      toaster.create({
        title: "Error",
        description: "Por favor corrige los errores en el formulario.",
        type: "error",
      });
    }
    return isValid;
  }

  function validateStep2(form: RegistrationForm): boolean {
    const newErrors = {
      username: validation.validateUsername(form.username),
      password: validation.validatePassword(
        form.password,
        false,
        true,
        form.confirmPassword,
      ),
      email: validation.validateEmail(form.email),
      phone: validation.validatePhone(form.phone),
    };

    setErrors((prev) => ({ ...prev, ...newErrors }));
    return !Object.values(newErrors).some((error) => error !== "");
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
      width="fit-content"
    >
      <VStack gap={4}>
        <Heading as="h1">Registro</Heading>
        {(step === 1 || step === 2) && (
          <>
            <TextSecondary>
              Solo serán públicos tu nombre de usuario y foto de perfil
            </TextSecondary>
            {errors.general && (
              <CustomAlert
                state="error"
                message={errors.general}
                closeable={false}
                maxWidth={"520px"}
              />
            )}
          </>
        )}

        {/* ====== Paso 1 ====== */}
        {step === 1 && (
          <>
            <CustomInput
              label="Nombre"
              name="name"
              placeholder="Introduce tu nombre"
              required={true}
              error={errors.name}
              onChange={(e) => handleChange(e, form, setErrors, setForm)}
              defaultValue={form.name}
            />
            <CustomInput
              label="Apellidos"
              name="surname"
              placeholder="Introduce tus apellidos"
              required={true}
              error={errors.surname}
              onChange={(e) => handleChange(e, form, setErrors, setForm)}
              defaultValue={form.surname}
            />
            <CustomInput
              label="DNI"
              name="dni"
              placeholder="Introduce tu DNI"
              required={true}
              error={errors.dni}
              onChange={(e) => handleChange(e, form, setErrors, setForm)}
              defaultValue={form.dni}
            />

            <UploadBox
              text={
                <>
                  Arrastra tu <b>carta de pago</b> en PDF
                </>
              }
              secondaryText="PDF, tamaño no superior a 2MB"
              fileType="application/pdf"
              onFileChange={(file) => {
                setErrors((prev) => ({ ...prev, general: "" }));
                setPaymentReceipt(file);
              }}
              disabled={loadingRegister}
            />

            <Field.Root invalid={!!errors.termsAccepted} required>
              <Checkbox.Root
                checked={termsAccepted}
                onCheckedChange={(e) => {
                  setErrors((prev) => ({ ...prev, termsAccepted: "" }));
                  setTermsAccepted(!!e.checked);
                }}
              >
                <Checkbox.HiddenInput />
                <Checkbox.Control />
                <Checkbox.Label>
                  He leído y acepto los{" "}
                  <Link
                    href="/terminos-de-uso"
                    target="_blank"
                    rel="noopener noreferrer"
                    color="principal.500"
                  >
                    Términos de Uso
                  </Link>{" "}
                  y la{" "}
                  <Link
                    href="/politica-de-privacidad"
                    target="_blank"
                    rel="noopener noreferrer"
                    color="principal.500"
                  >
                    Política de Privacidad
                  </Link>
                  .
                </Checkbox.Label>
              </Checkbox.Root>

              <Field.ErrorText>{errors.termsAccepted}</Field.ErrorText>
            </Field.Root>
            <Field.Root invalid={!!errors.rulesAccepted} required>
              <Checkbox.Root
                checked={rulesAccepted}
                onCheckedChange={(e) => {
                  setErrors((prev) => ({ ...prev, rulesAccepted: "" }));
                  setRulesAccepted(!!e.checked);
                }}
              >
                <Checkbox.HiddenInput />
                <Checkbox.Control />
                <Checkbox.Label>
                  He leído y acepto las{" "}
                  <Link
                    href="/normas"
                    target="_blank"
                    rel="noopener noreferrer"
                    color="principal.500"
                  >
                    Normas de Uso
                  </Link>
                  .
                </Checkbox.Label>
              </Checkbox.Root>

              <Field.ErrorText>{errors.rulesAccepted}</Field.ErrorText>
            </Field.Root>
            <CustomButton
              onClick={() => {
                const isValid = validateStep1(form);
                if (isValid) {
                  setStep(2);
                }
              }}
            >
              Continuar <IconArrowNarrowRight stroke={2} />
            </CustomButton>
          </>
        )}

        {/* ====== Paso 2 ====== */}
        {step == 2 && (
          <>
            <CustomInput
              label="Nombre de usuario"
              name="username"
              placeholder="Introduce tu nombre de usuario"
              required={true}
              error={errors.username}
              onChange={(e) => handleChange(e, form, setErrors, setForm)}
              defaultValue={form.username}
            />
            <CustomInput
              label="Contraseña"
              name="password"
              required={true}
              error={errors.password}
              onChange={(e) => handleChange(e, form, setErrors, setForm)}
              password={true}
              defaultValue={form.password}
            />
            <CustomInput
              label="Confirma tu contraseña"
              name="confirmPassword"
              required={true}
              onChange={(e) => handleChange(e, form, setErrors, setForm)}
              password={true}
              defaultValue={form.confirmPassword}
            />
            <CustomInput
              label="Correo electrónico"
              name="email"
              required={true}
              error={errors.email}
              onChange={(e) => handleChange(e, form, setErrors, setForm)}
              defaultValue={form.email}
            />
            <CustomInput
              label="Teléfono"
              name="phone"
              required={true}
              error={errors.phone}
              onChange={(e) => handleChange(e, form, setErrors, setForm)}
              defaultValue={form.phone}
            />

            <UploadBox
              text={
                <>
                  Arrastra tu <b>foto de perfil</b>
                </>
              }
              secondaryText="JPG o PNG, tamaño no superior a 2MB"
              fileType="image/*"
              onFileChange={setAvatar}
              disabled={loadingRegister}
            />
            {avatar?.name && (
              <Text fontSize="sm">Archivo subido: {avatar.name}</Text>
            )}
            <HStack>
              <CustomButton
                onClick={() => {
                  setStep(1);
                }}
                disabled={loadingRegister}
              >
                <IconArrowNarrowLeft stroke={2} /> Volver atrás
              </CustomButton>
              <CustomButton
                onClick={() => void handleSubmit()}
                loading={loadingRegister}
              >
                Registrarse
              </CustomButton>
            </HStack>

            <TextSecondary textAlign="center">
              No podrás iniciar sesión hasta que un colaborador apruebe tu
              registro. <br />
              Muchas gracias por tu paciencia.
            </TextSecondary>
          </>
        )}

        {/* ====== Paso 3 ====== */}
        {step == 3 && (
          <>
            <CustomAlert
              state="success"
              message="¡Registro solicitado correctamente!"
              closeable={false}
            />
            <Text textAlign="center">
              <b>RECUERDA:</b> <br />
              No podrás iniciar sesión hasta que un colaborador apruebe tu
              registro. <br />
              Muchas gracias por tu paciencia.
            </Text>
            <CustomButton onClick={() => void navigate("/iniciar-sesion")}>
              Intentar iniciar sesión ahora
            </CustomButton>
            <CustomButton onClick={() => void navigate("/")}>
              Volver al inicio
            </CustomButton>
          </>
        )}
      </VStack>
    </Flex>
  );
}
