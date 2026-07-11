import { Flex, Heading, HStack, Spinner, VStack } from "@chakra-ui/react";
import { IconArrowNarrowLeft } from "@tabler/icons-react";
import { useState, type ChangeEvent } from "react";
import { useNavigate } from "react-router-dom";

import {
  CustomAvatar,
  CustomButton,
  CustomInput,
  TextSecondary,
  toaster,
  UploadBox,
} from "@/modules/core/components";
import { useAuth } from "@/modules/core/context/useAuth";
import { handleChange } from "@/modules/core/utils/utils";

import { useUpdateUserProfile, useUpdateUserProfileAvatar } from "../../hooks";
import {
  MAX_LENGTH,
  validateUserProfileUpdateForm,
} from "../../validations/user.validations";

import type { UserProfileUpdateRequest } from "../../types";

export function EditProfilePage() {
  const { user, token } = useAuth();
  const navigate = useNavigate();

  const updateProfile = useUpdateUserProfile();
  const updateAvatar = useUpdateUserProfileAvatar();

  const [form, setForm] = useState<UserProfileUpdateRequest>({
    username: user?.username || "",
    name: user?.name || "",
    surname: user?.surname || "",
    email: user?.email || "",
    phone: user?.phone || "",
  });

  const [errors, setErrors] = useState<Record<string, string>>({
    username: "",
    password: "",
    name: "",
    surname: "",
    phone: "",
    email: "",
    general: "",
  });

  const handleFormChange = (
    value: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) => handleChange(value, form, setErrors, setForm);

  function validateForm(): boolean {
    const newErrors: Record<string, string> =
      validateUserProfileUpdateForm(form);
    setErrors(newErrors);
    return !Object.values(newErrors).some((v) => !!v);
  }

  function handleSubmit() {
    if (!token || !user) return;

    if (!validateForm()) {
      toaster.create({
        title: "Error",
        description: "Por favor corrige los errores en el formulario.",
        type: "error",
      });
      return;
    }

    const payload: UserProfileUpdateRequest = { ...form };

    if (!payload.password) {
      delete payload.password;
    }

    updateProfile.mutate({
      token,
      data: payload,
      oldUsername: user.username,
    });

    if (updateProfile.isError) {
      setErrors((prev) => ({
        ...prev,
        general: "Error: " + updateProfile.error?.message,
      }));
    }
  }

  function handleAvatarChange(file: File | null) {
    if (!token || !file || !user) return;

    updateAvatar.mutate({
      token,
      avatar: file,
      username: user.username,
    });

    if (updateAvatar.isError) {
      setErrors((prev) => ({
        ...prev,
        general:
          "Error al actualizar el avatar: " + updateAvatar.error?.message,
      }));
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
      width="fit-content"
    >
      <HStack w="100%" justify="space-between" align="center" mb={4}>
        <CustomButton
          color="transparent"
          onClick={() => void navigate("/perfil")}
        >
          <IconArrowNarrowLeft stroke={2} style={{ width: 32, height: 32 }} />
        </CustomButton>

        <Heading as="h1"> Perfil - {user?.username} </Heading>
        <HStack w="48px" />
      </HStack>
      {!user ? (
        <Spinner size="xl" borderWidth="4px" color="principal.800" />
      ) : (
        <VStack gap={4}>
          <HStack gap={4}>
            <CustomAvatar
              src={user?.avatar}
              name={form?.name || "User"}
              w="100px"
              h="100px"
              loading={updateAvatar.isPending}
            />

            <UploadBox
              text={
                <>
                  Arrastra la <b>foto de perfil</b>
                </>
              }
              secondaryText="JPG o PNG, tamaño no superior a 2MB"
              fileType="image/*"
              onFileChange={(file) => void handleAvatarChange(file)}
              disabled={updateAvatar.isPending}
            />
          </HStack>
          <CustomInput
            label="Nombre de usuario"
            name="username"
            error={errors.username}
            onChange={handleFormChange}
            defaultValue={form?.username}
            maxLength={MAX_LENGTH.USERNAME}
          />
          <CustomInput
            label="Nueva contraseña"
            name="password"
            password={true}
            error={errors.password}
            onChange={handleFormChange}
            maxLength={MAX_LENGTH.PASSWORD}
          />
          <CustomInput
            label="Nombre"
            name="name"
            error={errors.name}
            onChange={handleFormChange}
            defaultValue={form?.name}
            maxLength={MAX_LENGTH.NAME}
          />
          <CustomInput
            label="Apellidos"
            name="surname"
            error={errors.surname}
            onChange={handleFormChange}
            defaultValue={form?.surname}
            maxLength={MAX_LENGTH.SURNAME}
          />

          <CustomInput
            label="Correo electrónico"
            name="email"
            required={true}
            error={errors.email}
            onChange={handleFormChange}
            defaultValue={form?.email}
            maxLength={MAX_LENGTH.EMAIL}
          />

          <CustomInput
            label="Teléfono"
            name="phone"
            required={true}
            error={errors.phone}
            onChange={handleFormChange}
            defaultValue={form?.phone}
            maxLength={MAX_LENGTH.PHONE}
          />

          <TextSecondary>
            Para editar tu rol o tu DNI, contacta con{" "}
            <a
              href="mailto:cultura_etsii@us.es"
              style={{ color: "#4B759D", textDecoration: "underline" }}
            >
              cultura_etsii@us.es
            </a>
            .
          </TextSecondary>

          <CustomButton
            onClick={() => void handleSubmit()}
            loading={updateProfile.isPending}
            disabled={updateProfile.isPending}
          >
            Guardar cambios
          </CustomButton>
        </VStack>
      )}
    </Flex>
  );
}
