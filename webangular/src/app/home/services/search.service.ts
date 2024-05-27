import { Injectable, inject } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';

@Injectable({
  providedIn: 'root',
})
export class SearchService {
  private http: HttpClient = inject(HttpClient);

  search(pattern: string): Observable<any> {
    return this.http
      .get('https://localhost/api/search/user', {
        params: { pattern: pattern },
      })
      .pipe(
        catchError((error: HttpErrorResponse) =>
          of({ ...error.error, status: error.status })
        )
      );
  }
}
