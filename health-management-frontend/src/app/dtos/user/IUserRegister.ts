export interface IUserRegister {
  email: string;
  password: string;
  fullName: string;
  age: number | null;
  gender: 'MALE' | 'FEMALE' | 'OTHER' | string;
  weightKg: number | null;
  heightCm: number | null;
}
