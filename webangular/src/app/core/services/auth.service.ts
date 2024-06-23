import { Injectable } from '@angular/core';
import { User } from '../../auth/models/user.model';
import { Observable, catchError, map, of, pipe, tap, throwError } from 'rxjs';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Login } from '../../auth/models/login.model';

@Injectable({
  providedIn: 'root',
})
export class AuthService {
  private userToken: string | null | undefined;

  private currentUser: User | undefined;

  setCurrentUser(user: User) {
    this.currentUser = user;
  }

  getCurrentUser(): User | undefined {
    return this.currentUser;
  }

  getUserToken(): string | undefined | null {
    if (this.userToken == null || this.userToken == undefined) {
      this.userToken = localStorage.getItem('currentUser');
    }
    return this.userToken;
  }

  constructor(private http: HttpClient) {}

  register(user: User): Observable<Map<string, string>> {
    return this.http
      .post<Map<string, string>>('https://localhost/auth/register', user)
      .pipe(
        catchError((error: HttpErrorResponse) =>
          of({ ...error.error, status: error.status } as Map<string, string>)
        ),
        tap((event) => this.extractToken(event))
      );
  }

  login(login: Login): Observable<Map<string, string>> {
    return this.http
      .post<Map<string, string>>('https://localhost/auth/login', login)
      .pipe(
        catchError((error: HttpErrorResponse) =>
          of({ ...error.error, status: error.status } as Map<string, string>)
        ),
        tap((event) => this.extractToken(event))
      );
  }

  logout(): Observable<Map<string, string>> {
    return this.http
      .get<Map<string, string>>('https://localhost/auth/logout')
      .pipe(
        catchError((error: HttpErrorResponse) =>
          of({ ...error.error, status: error.status } as Map<string, string>)
        ),
      );
  }

  cleanUserToken(): void {
    this.userToken = null;
    localStorage.removeItem('currentUser');
  }

  extractToken(map: any): void {
    if (map['access_token'] != null) {
      this.userToken = map['access_token']
      localStorage.setItem('currentUser', map['access_token']);
    }
  }

  get isLogged(): boolean {
    return this.getUserToken() != undefined && this.getUserToken() != null;
  }
}
