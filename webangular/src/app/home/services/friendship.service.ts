import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable, catchError, of } from 'rxjs';
import {
  InvitationRequest,
  PendingRequest,
  User,
} from '../../auth/models/user.model';

export interface FriendshipResponseInterface {
  friends: User[];
  pendingInvitation: PendingRequest[];
  invitation: InvitationRequest[];
}

@Injectable({
  providedIn: 'root',
})
export class FriendshipService {
  private http: HttpClient = inject(HttpClient);

  friendship(): Observable<FriendshipResponseInterface> {
    return this.http
      .get<FriendshipResponseInterface>('https://localhost/api/friendships')
      .pipe(
        catchError((error: HttpErrorResponse) =>
          of({ ...error.error, status: error.status })
        )
      );
  }

  removeFriendShip(username: string): Observable<any> {
    return this.http
      .delete('https://localhost:/api/friendship', {
        params: { username: username },
      })
      .pipe(
        catchError((error: HttpErrorResponse) =>
          of({ ...error.error, status: error.status })
        )
      );
  }

  replyInvitation(username: string, response: boolean): Observable<any> {
    return this.http
      .put('https://localhost/api/friendship_request', {
        username: username,
        response: response,
      })
      .pipe(
        catchError((error: HttpErrorResponse) =>
          of({ ...error.error, status: error.status })
        )
      );
  }

  sendInvitation(username: string): Observable<any> {
    return this.http
      .post('https://localhost/api/friendship_request', {
        username: username,
      })
      .pipe(
        catchError((error: HttpErrorResponse) =>
          of({ ...error.error, status: error.status })
        )
      );
  }
}
