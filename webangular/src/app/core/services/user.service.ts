import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { EditPassword } from '../models/edit.password.model';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  constructor(private http: HttpClient) {}

  editPassword(editPassword: EditPassword): Observable<Map<string, string>> {
    return this.http
      .put<Map<string, string>>(
        'https://localhost/api/user/password',
        editPassword
      )
      .pipe(
        catchError((error: HttpErrorResponse) =>
          of({ ...error.error, status: error.status } as Map<string, string>)
        )
      );
  }
}
