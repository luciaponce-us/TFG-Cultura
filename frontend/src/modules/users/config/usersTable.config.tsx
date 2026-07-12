import { IconEye } from "@tabler/icons-react";

import { CustomAvatar, CustomButton } from "@/modules/core/components";
import { UserActions } from "../components";

import { parsePaymentReceiptUrl, parseRole } from "../utils";

import type { User } from "../types";

export const defaultHeaders = [
  "Avatar",
  "Username",
  "Nombre",
  "Rol",
  "DNI",
  "Teléfono",
  "Email",
  "Carta de Pago",
  "Activo",
  "Fecha de Creación",
  "Acciones",
];

export const mobileHeaders = [
  "Avatar",
  "Username",
  "Nombre",
  "Rol",
  "Creación",
  "Acciones",
];

export const mobileRowsContent = (user: User) => [
  {
    key: "avatar",
    content: (
      <CustomAvatar
        src={user.avatar || undefined}
        name={user.name}
        w="40px"
        h="40px"
      />
    ),
  },
  { key: "username", content: user.username },
  { key: "fullname", content: `${user.name} ${user.surname}` },
  { key: "role", content: parseRole(user.role) },
  {
    key: "created",
    content: new Date(user.createdAt)
      .toLocaleString("es-ES", {
        year: "2-digit",
        month: "2-digit",
        day: "2-digit",
      })
      .replace(",", ""),
  },
  {
    key: "actions",
    content: <UserActions user={user} />,
  },
];

export const defaultRowsContent = (user: User) => [
  {
    key: "avatar",
    content: (
      <CustomAvatar
        src={user.avatar || undefined}
        name={user.name}
        w="40px"
        h="40px"
      />
    ),
  },
  { key: "username", content: user.username },
  { key: "fullname", content: `${user.name} ${user.surname}` },
  { key: "role", content: parseRole(user.role) },
  { key: "dni", content: user.dni },
  { key: "phone", content: user.phone },
  { key: "email", content: user.email },
  {
    key: "payment",
    content: user.paymentReceipt ? (
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
    ) : (
      "No tiene"
    ),
  },
  { key: "active", content: user.active ? "Sí" : "No" },
  {
    key: "created",
    content: new Date(user.createdAt)
      .toLocaleString("es-ES", {
        year: "numeric",
        month: "2-digit",
        day: "2-digit",
        hour: "2-digit",
        minute: "2-digit",
      })
      .replace(",", ""),
  },
  {
    key: "actions",
    content: <UserActions user={user} />,
  },
];
