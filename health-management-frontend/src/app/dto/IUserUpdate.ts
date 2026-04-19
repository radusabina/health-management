export interface IUserUpdate {
  email: string;
  fullName: string;
  age: number | null;
  gender: 'MALE' | 'FEMALE' | 'OTHER' | string;
  weightKg: number | null;
  heightCm: number | null;
}
