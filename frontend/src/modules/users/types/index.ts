export interface UserRegisterRequest {
  username: string;
  password: string;
  name: string;
  surname: string;
  dni: string;
  phone: string;
  email: string;
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