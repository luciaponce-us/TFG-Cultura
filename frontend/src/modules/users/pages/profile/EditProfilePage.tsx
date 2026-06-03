import { Flex, Heading, HStack, Spinner, VStack } from "@chakra-ui/react";
import { useNavigate } from "react-router-dom";
import { useState, type ChangeEvent } from "react";
import { useAuth } from "@/modules/core/context/useAuth";
import {
  updateUserProfile,
  updateUserProfileAvatar,
} from "../../service/user.service";
import { toaster } from "@/modules/core/components/toaster/toaster";
import type { UserProfileUpdateRequest } from "../../types";
import {
  CustomButton,
  CustomInput,
  UploadBox,
  CustomAvatar,
  TextSecondary,
} from "@/modules/core/components";
import { IconArrowNarrowLeft } from "@tabler/icons-react";
import * as validation from "../../validations/user.validations";
import { handleChange, isApiError } from "@/modules/core/utils/utils";

export function EditProfilePage() {
  const { user, token, setUser } = useAuth();
  const navigate = useNavigate();

  const [loadingChanges, setLoadingChanges] = useState<boolean>(false);

  const [form, setForm] = useState<UserProfileUpdateRequest>({
    username: user?.username || "",
    name: user?.name || "",
    surname: user?.surname || "",
    email: user?.email || "",
    phone: user?.phone || "",
  });
  const [loadingAvatar, setLoadingAvatar] = useState<boolean>(false);

  const [errors, setErrors] = useState<Record<string, string>>({
    username: "",
    password: "",
    name: "",
    surname: "",
    phone: "",
    email: "",
    general: "",
  });

  const handleFormChange = (value: ChangeEvent<HTMLInputElement>) =>
    handleChange(value, form, setErrors, setForm);

  function validateForm(): boolean {
    const newErrors: Record<string, string> = {
      username: validation.validateUsername(form?.username || ""),
      password: validation.validatePassword(
        form?.password || "",
        true,
        false,
        undefined,
      ),
      name: validation.validateName(form?.name || ""),
      surname: validation.validateSurname(form?.surname || ""),
      phone: validation.validatePhone(form?.phone || ""),
      email: validation.validateEmail(form?.email || ""),
      general: "",
    };
    setErrors(newErrors);
    return !Object.values(newErrors).some((v) => !!v);
  }

  async function handleSubmit() {
    setLoadingChanges(true);
    if (!token || !form) {
      setLoadingChanges(false);
      return;
    }
    if (!validateForm()) {
      toaster.create({
        title: "Error",
        description: "Por favor corrige los errores en el formulario.",
        type: "error",
      });
      setLoadingChanges(false);
      return;
    }

    const payload: UserProfileUpdateRequest = { ...form };
    if (!payload.password) {
      delete payload.password;
    }

    try {
      const updatedUser = await updateUserProfile(token, payload);
      setUser(updatedUser);
      navigate(`/perfil`);

      toaster.create({
        title: "Éxito",
        description: `Tu perfil se ha actualizado correctamente.`,
        type: "success",
      });
    } catch (err) {
      console.error("Error al registrar usuario:", err);
      if (isApiError(err)) {
        setErrors({ ...errors, general: "Error: " + err.message });
        toaster.create({
          title: "Error",
          description: "No se pudo actualizar el usuario: " + err.message,
          type: "error",
        });
      }
    } finally {
      setLoadingChanges(false);
    }
  }

  async function handleAvatarChange(file: File | null) {
    if (!token || !file) return;

    try {
      setLoadingAvatar(true);
      const updatedUser = await updateUserProfileAvatar(token, file);
      setUser(updatedUser);
      toaster.create({
        title: "Éxito",
        description: "Tu foto de perfil se ha actualizado correctamente.",
        type: "success",
      });
    } catch (err) {
      console.error("Error al actualizar avatar:", err);
      if (isApiError(err)) {
        toaster.create({
          title: "Error",
          description:
            "No se pudo actualizar la foto de perfil: " + err.message,
          type: "error",
        });
      }
    } finally {
      setLoadingAvatar(false);
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
        <CustomButton color="transparent" onClick={() => navigate("/perfil")}>
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
              src={user?.avatar || "https://via.placeholder.com/150"}
              name={form?.name || "User"}
              w="100px"
              h="100px"
              loading={loadingAvatar}
            />

            <UploadBox
              text={
                <>
                  Arrastra la <b>foto de perfil</b>
                </>
              }
              secondaryText="JPG o PNG, tamaño no superior a 2MB"
              fileType="image/*"
              onFileChange={(file) => handleAvatarChange(file)}
              disabled={loadingAvatar}
            />
          </HStack>
          <CustomInput
            label="Nombre de usuario"
            name="username"
            error={errors.username}
            onChange={handleFormChange}
            defaultValue={form?.username}
          />
          <CustomInput
            label="Nueva contraseña"
            name="password"
            password={true}
            error={errors.password}
            onChange={handleFormChange}
          />
          <CustomInput
            label="Nombre"
            name="name"
            error={errors.name}
            onChange={handleFormChange}
            defaultValue={form?.name}
          />
          <CustomInput
            label="Apellidos"
            name="surname"
            error={errors.surname}
            onChange={handleFormChange}
            defaultValue={form?.surname}
          />

          <CustomInput
            label="Correo electrónico"
            name="email"
            required={true}
            error={errors.email}
            onChange={handleFormChange}
            defaultValue={form?.email}
          />

          <CustomInput
            label="Teléfono"
            name="phone"
            required={true}
            error={errors.phone}
            onChange={handleFormChange}
            defaultValue={form?.phone}
          />

          <TextSecondary>
            Para editar tu rol o tu DNI, contacta con <a href="mailto:cultura_etsii@us.es" style={{ color: "#4B759D", textDecoration: "underline" }}>cultura_etsii@us.es</a>.
          </TextSecondary>

          <CustomButton
            onClick={handleSubmit}
            loading={loadingChanges}
            disabled={loadingChanges}
          >
            Guardar cambios
          </CustomButton>
        </VStack>
      )}
    </Flex>
  );
}
