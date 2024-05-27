import {
  HttpEvent,
  HttpHandler,
  HttpHeaders,
  HttpInterceptor,
  HttpRequest,
} from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { AuthService } from '../services/auth.service';

@Injectable()
export class AuthInterceptor implements HttpInterceptor {
  constructor(private auth: AuthService) {}

  intercept(
    req: HttpRequest<any>,
    next: HttpHandler
  ): Observable<HttpEvent<any>> {
    let headers = new HttpHeaders();
    if (
      this.auth.getUserToken() != null &&
      this.auth.getUserToken() != undefined
    ) {
      headers = new HttpHeaders()
        .append('Authorization', `Bearer ${this.auth.getUserToken()}`);
    }
    const modifyRequest = req.clone({ headers });
    return next.handle(modifyRequest);
  }
}
