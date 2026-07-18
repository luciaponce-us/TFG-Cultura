import { HStack, VStack, Text } from "@chakra-ui/react";
import { useState, type ChangeEvent } from "react";
import {
  CustomButton,
  CustomInput,
  UploadBox,
  CustomAvatar,
  CustomSelect,
} from "@/modules/core/components";
import { IconEye, IconFileDollar } from "@tabler/icons-react";
import { useParams } from "react-router-dom";

import { handleChange, handleSelectChange } from "@/modules/core/utils/utils";
import { useAuth } from "@/modules/core/context/useAuth";
import { toaster } from "@/modules/core/components";

import {
  isLowerRole,
  mapUserToUserUpdateRequest,
  parsePaymentReceiptUrl,
  parseUrlFilename,
  roleOptions,
} from "../utils";
import {
  MAX_LENGTH,
  validateUserUpdateForm,
} from "../validations/user.validations";
import { useUpdateUser, useUpdateUserAvatar } from "../hooks";

import type { UserUpdateRequest, User, Role } from "../types";

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
  // TODO: Ask confirmation before changing role
  const { username } = useParams();
  const { user: loggedUser, token } = useAuth();
  const loggedUserRole: Role | undefined = loggedUser?.role ?? "SOCIO";
  const userIsLowerRole = isLowerRole(user?.role, loggedUserRole);

  const updateUserMutation = useUpdateUser();
  const updateAvatarMutation = useUpdateUserAvatar();

  const [form, setForm] = useState<UserUpdateRequest>(() =>
    mapUserToUserUpdateRequest(user),
  );

  const [errors, setErrors] = useState<Record<string, string>>(DEFAULT_ERRORS);

  function handleFormChange(
    e: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>,
  ) {
    handleChange(e, form, setErrors, setForm);
  }

  function validateForm(): boolean {
    const newErrors: Record<string, string> = validateUserUpdateForm(
      loggedUserRole,
      form,
    );
    setErrors(newErrors);
    return !Object.values(newErrors).some((v) => !!v);
  }

  function handleSubmit() {
    if (!token || !username) return;

    if (!validateForm()) {
      toaster.create({
        title: "Error",
        description: "Por favor corrige los errores en el formulario.",
        type: "error",
      });
      return;
    }

    const request: UserUpdateRequest =
      form.password === "" ? { ...form, password: undefined } : form;

    updateUserMutation.mutate({
      token,
      username,
      data: request,
    });

    if (updateUserMutation.isError) {
      setErrors((prev) => ({
        ...prev,
        general: "Error: " + updateUserMutation.error?.message,
      }));
    }
  }

  function handleAvatarChange(file: File | null) {
    if (!file || !token || !username) return;

    updateAvatarMutation.mutate({
      token,
      username,
      avatar: file,
    });
  }

  return (
    <VStack gap={4}>
      {!userIsLowerRole && (
        <>
          <HStack gap={4}>
            <CustomAvatar
              src={user?.avatar}
              name={form?.name || "User"}
              loading={updateAvatarMutation.isPending}
              w="100px"
              h="100px"
            />
            <VStack gap={1} align="start">
              <Text fontWeight="bold">
                {form?.name} {form?.surname}
              </Text>
              <Text>@{form?.username}</Text>
              <Text>{form?.email}</Text>
            </VStack>
          </HStack>
          <Text color="red.500" fontWeight="bold">
            No tienes permisos para editar este usuario.
          </Text>
          <CustomButton onClick={() => window.history.back()}>
            Volver atrás
          </CustomButton>
        </>
      )}
      {userIsLowerRole && (
        <>
          <HStack gap={4}>
            <CustomAvatar
              src={user?.avatar}
              name={form?.name || "User"}
              loading={updateAvatarMutation.isPending}
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
              onFileChange={handleAvatarChange}
              disabled={
                updateUserMutation.isPending || updateAvatarMutation.isPending
              }
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
            label="DNI"
            name="dni"
            error={errors.dni}
            onChange={handleFormChange}
            defaultValue={form?.dni}
            maxLength={MAX_LENGTH.DNI}
            showMaxLength={false}
          />
          <CustomSelect
            label="Rol"
            placeholder="Selecciona un rol"
            options={roleOptions(loggedUserRole)}
            defaultValue={[form?.role]}
            disabled={updateUserMutation.isPending}
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

          <HStack
            gap={4}
            color="principal.800"
            justify="space-between"
            align="center"
            w="100%"
          >
            <HStack gap={2} align="center">
              <IconFileDollar stroke={1.5} size={40} />
              <Text>
                Carta de pago: {parseUrlFilename(user?.paymentReceipt)}
              </Text>
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
            onClick={() => handleSubmit()}
            loading={updateUserMutation.isPending}
            disabled={updateUserMutation.isPending || !userIsLowerRole}
          >
            Guardar cambios
          </CustomButton>
        </>
      )}
    </VStack>
  );
}
