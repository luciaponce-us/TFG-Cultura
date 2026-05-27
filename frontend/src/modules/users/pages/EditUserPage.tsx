import { Flex, Heading, HStack, Spinner, VStack, Text } from "@chakra-ui/react";
import { useNavigate, useParams } from "react-router-dom";
import { useEffect, useState, type ChangeEvent } from "react";
import { useAuth } from "@/modules/core/context/useAuth";
import {
  getUserByUsername,
  updateUser,
  updateUserAvatar,
} from "../service/user.service";
import { toaster } from "@/modules/core/components/toaster/toaster";
import type { Role, User, UserUpdateRequest } from "../types";
import {
  CustomButton,
  CustomInput,
  UploadBox,
  CustomAvatar,
  CustomSelect,
} from "@/modules/core/components";
import {
  IconEye,
  IconFileDollar,
  IconArrowNarrowLeft,
} from "@tabler/icons-react";
import * as validation from "../validations/user.validations";
import { isApiError } from "@/modules/core/utils/utils";
import { parsePaymentReceiptUrl, parseUrl } from "../utils";

export default function EditUserPage() {
  const { username } = useParams();
  const { token } = useAuth();
  const navigate = useNavigate();

  const [loadingForm, setLoadingForm] = useState<boolean>(false);
  const [loadingChanges, setLoadingChanges] = useState<boolean>(false);
  const [loadingAvatar, setLoadingAvatar] = useState<boolean>(false);

  const [user, setUser] = useState<User | null>(null);
  const [form, setForm] = useState<UserUpdateRequest | null>(null);
  const [avatar, setAvatar] = useState<File | null>(null);

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

  function resetErrors() {
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
  }

  const handleChange = (e: ChangeEvent<HTMLInputElement>) => {
    resetErrors();
    setForm({
      ...form,
      [e.target.name]: e.target.value,
    } as UserUpdateRequest);
  };

  const handleRoleChange = ({ value }: { value: string[] }) => {
    if (!value[0]) return;
    resetErrors();
    setForm((prev) =>
      prev ? { ...prev, role: value[0] as Role } : prev,
    );
  };

  useEffect(() => {
    if (!token || !username) return;

    async function fetchUser() {
      setLoadingForm(true);
      try {
        const userData = await getUserByUsername(token!, username!);
        setUser(userData);
        setForm({
          username: userData.username,
          password: "",
          name: userData.name,
          surname: userData.surname,
          dni: userData.dni,
          phone: userData.phone,
          email: userData.email,
          active: userData.active,
          role: userData.role,
        });
      } catch (error) {
        console.error("Error fetching user:", error);
        toaster.create({
          title: "Error",
          description: "No se pudo cargar el usuario.",
          type: "error",
        });
      } finally {
        setLoadingForm(false);
      }
    }

    fetchUser();
  }, [token, username]);

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
      dni: validation.validateDni(form?.dni || ""),
      phone: validation.validatePhone(form?.phone || ""),
      email: validation.validateEmail(form?.email || ""),
      general: "",
    };
    setErrors(newErrors);
    return !Object.values(newErrors).some((v) => !!v);
  }

  async function handleSubmit() {
    setLoadingChanges(true);
    if (!token || !username || !form) return;
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
      delete form.password;
    }

    try {
      const res = await updateUser(token!, username!, form!);
      if (form.username !== username) {
        navigate(`/admin/usuarios/${form.username}`);
      } else {
        navigate(`/admin/usuarios`);
      }
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
        const updatedUser = await updateUserAvatar(token, username, avatar);
        setUser(updatedUser);
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

    handleAvatarUpload();
  }, [avatar, token, username]);

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
          onClick={() => navigate("/admin/usuarios")}
        >
          <IconArrowNarrowLeft stroke={2} style={{ width: 32, height: 32 }} />
        </CustomButton>

        <Heading as="h1"> Perfil - {username} </Heading>
        <HStack w="48px" />
      </HStack>
      {loadingForm ? (
        <Spinner size="xl" borderWidth="4px" color="principal.800" />
      ) : (
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
            onChange={handleChange}
            defaultValue={form?.username}
          />
          <CustomInput
            label="Nueva contraseña"
            name="password"
            password={true}
            error={errors.password}
            onChange={handleChange}
          />
          <CustomInput
            label="Nombre"
            name="name"
            error={errors.name}
            onChange={handleChange}
            defaultValue={form?.name}
          />
          <CustomInput
            label="Apellidos"
            name="surname"
            error={errors.surname}
            onChange={handleChange}
            defaultValue={form?.surname}
          />
          <CustomInput
            label="DNI"
            name="dni"
            error={errors.dni}
            onChange={handleChange}
            defaultValue={form?.dni}
          />
          <CustomSelect
            label="Rol"
            placeholder="Selecciona un rol"
            options={[
              { label: "Socio", value: "SOCIO" as Role },
              { label: "Colaborador", value: "COLABORADOR" as Role },
              { label: "Encargado", value: "ENCARGADO" as Role },
              { label: "Secretario", value: "SECRETARIO" as Role },
              { label: "Coordinador", value: "COORDINADOR" as Role },
            ]}
            defaultValue={[form?.role as string]}
            disabled={loadingChanges}
            error={errors.role}
            onValueChange={handleRoleChange}
          />
          <CustomInput
            label="Correo electrónico"
            name="email"
            required={true}
            error={errors.email}
            onChange={handleChange}
            defaultValue={form?.email}
          />

          <CustomInput
            label="Teléfono"
            name="phone"
            required={true}
            error={errors.phone}
            onChange={handleChange}
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
              <Text>Carta de pago: {parseUrl(user?.paymentReceipt || "")}</Text>
            </HStack>
            <CustomButton
              onClick={() =>
                window.open(
                  parsePaymentReceiptUrl(user?.paymentReceipt as string),
                  "_blank",
                  "noopener,noreferrer",
                )
              }
            >
              <IconEye stroke={2} /> Ver
            </CustomButton>
          </HStack>
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
