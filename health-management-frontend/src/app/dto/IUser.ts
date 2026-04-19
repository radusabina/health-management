export interface IUser {
  id: string;
  email: string;
  fullName: string;
  age: number | null;
  gender: 'MALE' | 'FEMALE' | 'OTHER' | string;
  weightKg: number | null;
  heightCm: number | null;
  createdAt: string;
}
