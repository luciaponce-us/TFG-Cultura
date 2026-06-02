export interface UserRegisterRequest {
  username: string;
  password: string;
  name: string;
  surname: string;
  dni: string;
  phone: string;
  email: string;
}

export interface UserLoginRequest {
  username: string;
  password: string;
}

export interface UserUpdateRequest {
  username: string;
  password?: string;
  name: string;
  surname: string;
  dni: string;
  phone: string;
  email: string;
  active: boolean;
  role: Role;
}

export interface User {
  username: string;
  name: string;
  surname: string;
  dni: string;
  phone: string;
  email: string;
  avatar: string | null;
  paymentReceipt: string | null;
  active: boolean;
  role: Role;
  createdAt: string; // LocalDateTime → ISO string
}

export type Role =
  | "COORDINADOR"
  | "SECRETARIO"
  | "ENCARGADO"
  | "COLABORADOR"
  | "SOCIO";

export const MANAGEMENT_ROLES: Role[] = [
  "COORDINADOR",
  "SECRETARIO",
  "ENCARGADO",
  "COLABORADOR",
];
