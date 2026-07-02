import { HStack, VStack, Text } from "@chakra-ui/react";
import { useCallback, useEffect, useState, type ChangeEvent } from "react";
import { useQueryClient } from "@tanstack/react-query";
import {
  CustomButton,
  CustomInput,
  UploadBox,
  CustomAvatar,
  CustomSelect,
} from "@/modules/core/components";
import { IconEye, IconFileDollar } from "@tabler/icons-react";
import { useNavigate, useParams } from "react-router-dom";

import {
  handleChange,
  handleSelectChange,
  isApiError,
} from "@/modules/core/utils/utils";
import { useAuth } from "@/modules/core/context/useAuth";
import { toaster } from "@/modules/core/components/toaster/toaster";

import { updateUser, updateUserAvatar } from "../service/user.service";
import {
  mapUserToUserUpdateRequest,
  parsePaymentReceiptUrl,
  parseUrlFilename,
  roleOptions,
} from "../utils";
import { validateUserUpdateForm } from "../validations/user.validations";

import type { UserUpdateRequest, User } from "../types";

const DEFAULT_ERRORS: Record<string, string> = {
  username: "",
  password: "",
  name: "",
  surname: "",
  dni: "",
  phone: "",
  email: "",
  general: "",
};

export function EditUserForm({ user }: { readonly user: User }) {
  const { username } = useParams();
  const { token } = useAuth();
  const navigate = useNavigate();
  const queryClient = useQueryClient();

  const [loadingChanges, setLoadingChanges] = useState<boolean>(false);
  const [loadingAvatar, setLoadingAvatar] = useState<boolean>(false);
  const [form, setForm] = useState<UserUpdateRequest>(() => mapUserToUserUpdateRequest(user));
  const [avatar, setAvatar] = useState<File | null>(null);
  const [errors, setErrors] = useState<Record<string, string>>(DEFAULT_ERRORS);

  const resetErrors = useCallback(() => {
    setErrors(DEFAULT_ERRORS);
  }, []);

  function handleFormChange(
    e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) {
    handleChange(e, form, setErrors, setForm);
  }

  function validateForm(): boolean {
    const newErrors: Record<string, string> = validateUserUpdateForm(form);
    setErrors(newErrors);
    return !Object.values(newErrors).some((v) => !!v);
  }

  async function handleSubmit() {
    setLoadingChanges(true);
    if (!token || !username || !form) return;
    resetErrors();
    if (!validateForm()) {
      toaster.create({
        title: "Error",
        description: "Por favor corrige los errores en el formulario.",
        type: "error",
      });
      setLoadingChanges(false);
      return;
    }

    if (form.password === "") {
      setForm((prev) => (prev ? { ...prev, password: undefined } : prev));
    }

    try {
      const res = await updateUser(token, username, form);
      const isUsernameChanged = form.username !== username;
      const nextUrl = isUsernameChanged
        ? `/admin/usuarios/${form.username}`
        : `/admin/usuarios`;
      void navigate(nextUrl);

      toaster.create({
        title: "Éxito",
        description: `Usuario "${res.username}" actualizado correctamente.`,
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

  useEffect(() => {
    async function handleAvatarUpload() {
      if (!avatar || !token || !username) return;
      try {
        setLoadingAvatar(true);
        await updateUserAvatar(token, username, avatar);
        await queryClient.invalidateQueries({
          queryKey: ["user", username],
        });
      } catch (error) {
        console.error("Error al actualizar el avatar:", error);
        toaster.create({
          title: "Error",
          description: "No se pudo actualizar el avatar.",
          type: "error",
        });
      } finally {
        setLoadingAvatar(false);
      }
    }

    void handleAvatarUpload();
  }, [avatar, token, username, queryClient]);

  return (
    <VStack gap={4}>
      <HStack gap={4}>
        <CustomAvatar
          src={user?.avatar || "https://via.placeholder.com/150"}
          name={form?.name || "User"}
          loading={loadingAvatar}
          w="100px"
          h="100px"
        />

        <UploadBox
          text={
            <>
              Arrastra la <b>foto de perfil</b>
            </>
          }
          secondaryText="JPG o PNG, tamaño no superior a 2MB"
          fileType="image/*"
          onFileChange={setAvatar}
          disabled={loadingChanges}
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
        label="DNI"
        name="dni"
        error={errors.dni}
        onChange={handleFormChange}
        defaultValue={form?.dni}
      />
      <CustomSelect
        label="Rol"
        placeholder="Selecciona un rol"
        options={roleOptions}
        defaultValue={[form?.role]}
        disabled={loadingChanges}
        error={errors.role}
        onValueChange={(e) =>
          handleSelectChange(e.value, "role", form, setErrors, setForm)
        }
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

      <HStack
        gap={4}
        color="principal.800"
        justify="space-between"
        align="center"
        w="100%"
      >
        <HStack gap={2} align="center">
          <IconFileDollar stroke={1.5} size={40} />
          <Text>Carta de pago: {parseUrlFilename(user?.paymentReceipt)}</Text>
        </HStack>
        <CustomButton
          onClick={() =>
            window.open(
              parsePaymentReceiptUrl(user?.paymentReceipt),
              "_blank",
              "noopener,noreferrer",
            )
          }
        >
          <IconEye stroke={2} /> Ver
        </CustomButton>
      </HStack>
      <CustomButton
        onClick={() => void handleSubmit()}
        loading={loadingChanges}
        disabled={loadingChanges}
      >
        Guardar cambios
      </CustomButton>
    </VStack>
  );
}
