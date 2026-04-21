import { IUser } from '../../services/user/user.service';

export interface IUserLoginResponse {
  user: IUser;
  accessToken: string;
  refreshToken: string;
}
