import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IUserLogin } from '../../dto/IUserLogin';
import { IUserLoginResponse } from '../../dto/IUserLoginResponse';
import { endpointAPI } from '../../config/appconfig';
import { Observable, BehaviorSubject } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private storageKey = 'authResponse';
  private authSubject = new BehaviorSubject<IUserLoginResponse | null>(
    this.loadFromStorage(),
  );

  constructor(private http: HttpClient) {}

  login(credentials: IUserLogin): Observable<IUserLoginResponse> {
    return this.http.post<IUserLoginResponse>(
      endpointAPI + 'auth/login',
      credentials,
    );
  }

  setAuth(response: IUserLoginResponse): void {
    try {
      localStorage.setItem(this.storageKey, JSON.stringify(response));
      this.authSubject.next(response);
    } catch (e) {
      console.error('Failed to save auth response', e);
    }
  }

  clearAuth(): void {
    try {
      localStorage.removeItem(this.storageKey);
    } catch (e) {
      console.error('Failed to clear auth response', e);
    }
    this.authSubject.next(null);
  }

  getToken(): string | null {
    const current = this.authSubject.value;
    return current ? current.accessToken : null;
  }

  getAuthResponse(): IUserLoginResponse | null {
    return this.authSubject.value;
  }

  isLoggedIn(): boolean {
    return !!this.getToken();
  }

  private loadFromStorage(): IUserLoginResponse | null {
    try {
      const raw = localStorage.getItem(this.storageKey);
      return raw ? (JSON.parse(raw) as IUserLoginResponse) : null;
    } catch (e) {
      return null;
    }
  }
}
