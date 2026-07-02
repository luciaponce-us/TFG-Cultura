import { HStack, VStack, Text } from "@chakra-ui/react";
import { useCallback, useState, type ChangeEvent } from "react";
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
import { toaster } from "@/modules/core/components";

import {
  mapUserToUserUpdateRequest,
  parsePaymentReceiptUrl,
  parseUrlFilename,
  roleOptions,
} from "../utils";
import { validateUserUpdateForm } from "../validations/user.validations";

import type { UserUpdateRequest, User } from "../types";
import { useUpdateUser, useUpdateUserAvatar } from "../hooks";

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
  const updateUserMutation = useUpdateUser();

  const loadingChanges: boolean = updateUserMutation.isPending;
  const updateAvatarMutation = useUpdateUserAvatar();
  const loadingAvatar = updateAvatarMutation.isPending;

  const [form, setForm] = useState<UserUpdateRequest>(() =>
    mapUserToUserUpdateRequest(user),
  );

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

  function onSuccessUpdate(request: UserUpdateRequest, res: User) {
    const isUsernameChanged = request.username !== username;

    void navigate(
      isUsernameChanged
        ? `/admin/usuarios/${request.username}`
        : "/admin/usuarios",
    );

    toaster.create({
      title: "Éxito",
      description: `Usuario "${res.username}" actualizado correctamente.`,
      type: "success",
    });
  }

  function onErrorUpdate(err: Error) {
    console.error(err);

    if (isApiError(err)) {
      setErrors((prev) => ({
        ...prev,
        general: "Error: " + err.message,
      }));

      toaster.create({
        title: "Error",
        description: "No se pudo actualizar el usuario: " + err.message,
        type: "error",
      });
    } else {
      toaster.create({
        title: "Error",
        description:
          "Ha ocurrido un error inesperado. No se pudo actualizar el usuario.",
        type: "error",
      });
    }
  }

  function handleSubmit() {
    if (!token || !username) return;

    resetErrors();

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

    updateUserMutation.mutate(
      {
        token,
        username,
        data: request,
      },
      {
        onSuccess: (res) => {
          onSuccessUpdate(request, res);
        },
        onError: (err) => {
          onErrorUpdate(err);
        },
      },
    );
  }

  function handleAvatarChange(file: File | null) {
    if (!file || !token || !username) return;

    updateAvatarMutation.mutate(
      {
        token,
        username,
        avatar: file,
      },
      {
        onError: (error) => {
          console.error("Error al actualizar el avatar:", error);

          toaster.create({
            title: "Error",
            description: "No se pudo actualizar el avatar.",
            type: "error",
          });
        },
      },
    );
  }

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
          onFileChange={handleAvatarChange}
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
        onClick={() => handleSubmit()}
        loading={loadingChanges}
        disabled={loadingChanges}
      >
        Guardar cambios
      </CustomButton>
    </VStack>
  );
}
