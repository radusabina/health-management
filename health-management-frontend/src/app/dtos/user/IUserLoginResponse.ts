import { IUser } from '../user/IUser';

export interface IUserLoginResponse {
  user: IUser;
  accessToken: string;
  refreshToken: string;
}
