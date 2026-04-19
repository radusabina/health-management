import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { endpointAPI } from '../../config/appconfig';
import { Observable } from 'rxjs';
import { IUser } from '../../dto/IUser';
import { IUserUpdate } from '../../dto/IUserUpdate';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) {}

  getUserById(id: string): Observable<IUser> {
    return this.http.get<IUser>(endpointAPI + 'user/' + id);
  }

  isPasswordValid(password: string): Observable<boolean> {
    return this.http.get<boolean>(endpointAPI + 'user/is-password-valid', {
      params: { password },
    });
  }

  deleteUser(id: string): Observable<void> {
    return this.http.delete<void>(endpointAPI + 'user/' + id);
  }

  updateUser(id: string, userData: IUserUpdate): Observable<void> {
    return this.http.put<void>(endpointAPI + 'user/' + id, userData);
  }

  updatePassword(id: string, newPassword: string): Observable<void> {
    return this.http.put<void>(endpointAPI + 'user/password/' + id, {
      newPassword,
    });
  }
}
