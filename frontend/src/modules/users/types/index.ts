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
  role: Role;
}

export interface UserProfileUpdateRequest {
  username?: string;
  password?: string;
  name?: string;
  surname?: string;
  phone?: string;
  email?: string;
}

export interface FiltersGetAllUsers {
  name?: string;
  role?: string;
  active?: string;
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

export const ROLES = [
  "SOCIO",
  "COLABORADOR",
  "ENCARGADO",
  "SECRETARIO",
  "COORDINADOR",
] as const;

export type Role = (typeof ROLES)[number];

export const MANAGEMENT_ROLES: Role[] = [
  "COORDINADOR",
  "SECRETARIO",
  "ENCARGADO",
  "COLABORADOR",
];
