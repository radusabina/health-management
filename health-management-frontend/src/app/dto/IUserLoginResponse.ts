import { IUser } from './IUser';

export interface IUserLoginResponse {
  user: IUser;
  accessToken: string;
  refreshToken: string;
}
