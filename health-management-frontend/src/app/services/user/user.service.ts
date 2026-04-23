import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { endpointAPI } from '../../config/appconfig';
import { Observable } from 'rxjs';
import { IIsNewUserResponse } from '../../dtos/user/IIsNewUserResponse';
import { IUser } from '../../dtos/user/IUser';
import { IUserUpdate } from '../../dtos/user/IUserUpdate';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private baseUrl = endpointAPI + 'user';
  constructor(private http: HttpClient) {}

  getUserById(id: string): Observable<IUser> {
    return this.http.get<IUser>(endpointAPI + 'user/' + id);
  }

  isPasswordValid(password: string, userId: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/is-password-valid`, {
      params: { password, userId },
    });
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(`${this.baseUrl}/${id}`);
  }

  updateUser(id: string, userData: IUserUpdate): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/${id}`, userData);
  }

  updatePassword(id: string, newPassword: string): Observable<void> {
    return this.http.put<void>(`${this.baseUrl}/password/${id}`, {
      newPassword,
    });
  }

  isNewUser(userId: string): Observable<IIsNewUserResponse> {
    return this.http.get<IIsNewUserResponse>(
      `${endpointAPI}user/is-new-user/${userId}`,
    );
  }
}