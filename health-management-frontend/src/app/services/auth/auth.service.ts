import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { IUserLogin } from '../../dto/IUserLogin';
import { IUserLoginResponse } from '../../dto/IUserLoginResponse';
import { endpointAPI } from '../../config/appconfig';
import { Observable, BehaviorSubject, throwError } from 'rxjs';
import { filter, take, map, tap, finalize, catchError } from 'rxjs/operators';
import { IUserRegister } from '../../dto/IUserRegister';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private storageKey = 'authResponse';
  private authSubject = new BehaviorSubject<IUserLoginResponse | null>(
    this.loadFromStorage(),
  );

  private refreshInProgress = false;
  private refreshSubject = new BehaviorSubject<string | null>(null);

  constructor(private http: HttpClient) {}

  login(credentials: IUserLogin): Observable<IUserLoginResponse> {
    return this.http.post<IUserLoginResponse>(
      endpointAPI + 'auth/login',
      credentials,
    );
  }

  register(user: IUserRegister): Observable<any> {
    return this.http.post(endpointAPI + 'auth/register', user);
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

  private updateAccessToken(newAccessToken: string): void {
    const current = this.authSubject.value;
    if (!current) return;
    const updated: IUserLoginResponse = {
      ...current,
      accessToken: newAccessToken,
    };
    this.setAuth(updated);
  }

  refreshToken(): Observable<string> {
    const current = this.authSubject.value;
    const refreshToken = current?.refreshToken;
    if (!refreshToken) {
      return throwError(() => new Error('No refresh token available'));
    }

    if (this.refreshInProgress) {
      return this.refreshSubject.pipe(
        filter((t): t is string => t != null),
        take(1),
      ) as Observable<string>;
    }

    this.refreshInProgress = true;
    this.refreshSubject.next(null);

    const params = new HttpParams().set('refreshToken', refreshToken);

    return this.http
      .post<{
        accessToken: string;
      }>(endpointAPI + 'auth/refresh', null, { params })
      .pipe(
        map((res) => res.accessToken),
        tap((newToken) => {
          this.updateAccessToken(newToken);
          this.refreshSubject.next(newToken);
        }),
        finalize(() => {
          this.refreshInProgress = false;
        }),
        catchError((err) => {
          this.refreshSubject.next(null);
          this.clearAuth();
          return throwError(() => err);
        }),
      );
  }
}
