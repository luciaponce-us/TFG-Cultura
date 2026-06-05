import {
  VStack,
  Heading,
  Flex,
  Text,
  HStack,
  Checkbox,
  Link,
} from "@chakra-ui/react";
import { useState, type ChangeEvent } from "react";
import { useNavigate } from "react-router-dom";
import { registerUser } from "../service/user.service";
import type { UserRegisterRequest } from "../types";
import { isApiError } from "@/modules/core/utils/utils";
import {
  CustomAlert,
  CustomButton,
  TextSecondary,
  CustomInput,
  UploadBox,
} from "@/modules/core/components";
import { IconArrowNarrowLeft, IconArrowNarrowRight } from "@tabler/icons-react";
import * as validation from "../validations/user.validations";

type RegistrationForm = UserRegisterRequest & {
  confirmPassword: string;
};

export default function RegistrationPage() {
  const navigate = useNavigate();
  const [form, setForm] = useState<RegistrationForm>({
    username: "",
    password: "",
    name: "",
    surname: "",
    dni: "",
    phone: "",
    email: "",
    confirmPassword: "",
  });
  const [avatar, setAvatar] = useState<File | null>(null);
  const [paymentReceipt, setPaymentReceipt] = useState<File | null>(null);
  const [termsAccepted, setTermsAccepted] = useState(false);
  const [rulesAccepted, setRulesAccepted] = useState(false);

  const [errors, setErrors] = useState<Record<string, string>>({
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
  });

  const [step, setStep] = useState(1);
  const [loadingRegister, setLoadingRegister] = useState(false);

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async () => {
    setErrors({
      username: "",
      password: "",
      name: "",
      surname: "",
      dni: "",
      phone: "",
      email: "",
      general: paymentReceipt ? "" : "La carta de pago es obligatoria",
      termsAccepted: termsAccepted
        ? ""
        : "Debes aceptar los términos y condiciones",
      rulesAccepted: rulesAccepted ? "" : "Debes aceptar las normas de uso",
    });

    const isValid = validateStep2(form);
    if (!isValid || !paymentReceipt) return;
    try {
      setLoadingRegister(true);
      await registerUser(form, paymentReceipt, avatar || undefined);
      setStep(3);
      setForm({
        username: "",
        password: "",
        name: "",
        surname: "",
        dni: "",
        phone: "",
        email: "",
        confirmPassword: "",
      });
    } catch (err) {
      console.error("Error al registrar usuario:", err);
      if (isApiError(err))
        setErrors({ ...errors, general: "Error: " + err.message });
    } finally {
      setLoadingRegister(false);
    }
  };

  const validateStep1 = (form: RegistrationForm): boolean => {
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
    return !Object.values(newErrors).some((error) => error !== "");
  };

  const validateStep2 = (form: RegistrationForm): boolean => {
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

        {step === 1 && (
          <>
            <CustomInput
              label="Nombre"
              name="name"
              placeholder="Introduce tu nombre"
              required={true}
              error={errors.name}
              onChange={handleChange}
              defaultValue={form.name}
            />
            <CustomInput
              label="Apellidos"
              name="surname"
              placeholder="Introduce tus apellidos"
              required={true}
              error={errors.surname}
              onChange={handleChange}
              defaultValue={form.surname}
            />
            <CustomInput
              label="DNI"
              name="dni"
              placeholder="Introduce tu DNI"
              required={true}
              error={errors.dni}
              onChange={handleChange}
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
              onFileChange={setPaymentReceipt}
            />
            {paymentReceipt?.name && (
              <Text fontSize="sm">Archivo subido: {paymentReceipt.name}</Text>
            )}
            <Checkbox.Root
              required
              checked={termsAccepted}
              onCheckedChange={(e) => setTermsAccepted(!!e.checked)}
              invalid={!!errors.termsAccepted}
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
            <Checkbox.Root
              required
              checked={rulesAccepted}
              onCheckedChange={(e) => setRulesAccepted(!!e.checked)}
              invalid={!!errors.rulesAccepted}
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
        {step == 2 && (
          <>
            <CustomInput
              label="Nombre de usuario"
              name="username"
              placeholder="Introduce tu nombre de usuario"
              required={true}
              error={errors.username}
              onChange={handleChange}
              defaultValue={form.username}
            />
            <CustomInput
              label="Contraseña"
              name="password"
              required={true}
              error={errors.password}
              onChange={handleChange}
              password={true}
              defaultValue={form.password}
            />
            <CustomInput
              label="Confirma tu contraseña"
              name="confirmPassword"
              required={true}
              onChange={handleChange}
              password={true}
              defaultValue={form.confirmPassword}
            />
            <CustomInput
              label="Correo electrónico"
              name="email"
              required={true}
              error={errors.email}
              onChange={handleChange}
              defaultValue={form.email}
            />
            <CustomInput
              label="Teléfono"
              name="phone"
              required={true}
              error={errors.phone}
              onChange={handleChange}
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
              <CustomButton onClick={handleSubmit} loading={loadingRegister}>
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
            <CustomButton onClick={() => navigate("/iniciar-sesion")}>
              Intentar iniciar sesión ahora
            </CustomButton>
            <CustomButton onClick={() => navigate("/")}>
              Volver al inicio
            </CustomButton>
          </>
        )}
      </VStack>
    </Flex>
  );
}
