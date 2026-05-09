import { VStack, Heading, Flex } from "@chakra-ui/react";
import { useState, type ChangeEvent } from "react";
import { registerUser } from "../service/user.service";
import type { UserRegisterRequest } from "../types";
import { isApiError } from "@/modules/core/utils/utils";
import {
  CustomAlert,
  CustomButton,
  TextSecondary,
  CustomInput,
  UploadBox
} from "@/modules/core/components";
import { IconArrowNarrowRight } from "@tabler/icons-react";
import * as validation from "../validations/user.validations";

export default function RegistrationPage() {
  const [form, setForm] = useState<UserRegisterRequest>({
    username: "",
    password: "",
    name: "",
    surname: "",
    dni: "",
    phone: "",
    email: "",
  });

  const [avatar, setAvatar] = useState<File | null>(null);
  const [paymentReceipt, setPaymentReceipt] = useState<File | null>(null);
  
  const [errors, setErrors] = useState<Record<string, string>>({
    username: "",
    password: "",
    name: "",
    surname: "",
    dni: "",
    phone: "",
    email: "",
    general: "",
  });

  const [step, setStep] = useState(1);

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    });
  };

  const handleSubmit = async () => {
    try {
      setErrors({
        username: "",
        password: "",
        name: "",
        surname: "",
        dni: "",
        phone: "",
        email: "",
        general: "",
      });
      if (!paymentReceipt) {
        setErrors((prev) => ({ ...prev, general: "La carta de pago es obligatoria" }));
        return;
      }
      await registerUser(form, paymentReceipt);
      alert("Usuario registrado y logueado");
    } catch (err) {
      if (isApiError(err))
        setErrors({ ...errors, general: "No se pudo registrar" + err.message });
    }
  };

  const validateStep1 = (form: UserRegisterRequest): boolean => {
    const newErrors = {
      name: validation.validateName(form.name),
      surname: validation.validateSurname(form.surname),
      dni: validation.validateDni(form.dni),
      general: paymentReceipt ? "":"La carta de pago es obligatoria"
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

        <TextSecondary>
          Solo serán públicos tu nombre de usuario y foto de perfil
        </TextSecondary>
        {errors.general && (
          <CustomAlert state="error" message={errors.general} closeable={false} />
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
            />
            <CustomInput
              label="Apellidos"
              name="surname"
              placeholder="Introduce tus apellidos"
              required={true}
              error={errors.surname}
              onChange={handleChange}
            />
            <CustomInput
              label="DNI"
              name="dni"
              placeholder="Introduce tu DNI"
              required={true}
              error={errors.dni}
              onChange={handleChange}
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
        {step == 2 && <></>}
      </VStack>
    </Flex>
  );
}
