import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { endpointAPI } from '../../config/appconfig';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private baseUrl = endpointAPI + 'user';
  constructor(private http: HttpClient) {}

  getUserById(id: string): Observable<IUser> {
    return this.http.get<IUser>(endpointAPI + 'user/' + id);
  }

  isPasswordValid(password: string): Observable<boolean> {
    return this.http.get<boolean>(`${this.baseUrl}/is-password-valid`, {
      params: { password },
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

export interface IUser {
  id: string;
  email: string;
  fullName: string;
  age: number | null;
  gender: 'MALE' | 'FEMALE' | 'OTHER' | string;
  weightKg: number | null;
  heightCm: number | null;
}

export interface IUserUpdate {
  email: string;
  fullName: string;
  age: number | null;
  gender: 'MALE' | 'FEMALE' | 'OTHER' | string;
  weightKg: number | null;
  heightCm: number | null;
}

export interface IIsNewUserResponse {
  isNewUser: boolean;
}
