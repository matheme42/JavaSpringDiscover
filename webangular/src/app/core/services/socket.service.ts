import { Injectable, inject } from '@angular/core';
import { AuthService } from './auth.service';
import { Observable, Subject } from 'rxjs';
import { Router } from '@angular/router';

@Injectable({
  providedIn: 'root',
})
export class SocketService {
  private webSocket?: WebSocket;

  private authService: AuthService = inject(AuthService);

  private router: Router = inject(Router);

  private messageSubject$: Subject<any> = new Subject<any>();

  private statusSubject$: Subject<any> = new Subject<any>();

  connect(): void {
    if (!this.authService.isLogged) return;

    this.statusSubject$.next(undefined);
    this.webSocket = new WebSocket(
      `wss://localhost/ws?Authorization=${this.authService.getUserToken()}`
    );

    this.webSocket.onclose = (event: any) => this.privateOnClose(event);
    this.webSocket.onmessage = (event: any) => this.privateOnMessage(event);
    this.webSocket.onerror = (error: any) => this.privateOnError(error);
  }

  public disconnect() {
    const webSocket: WebSocket | undefined = this.webSocket;
    this.webSocket = undefined;
    webSocket?.close();
  }

  private privateOnClose(event: any) {
    if (this.webSocket == undefined) {
      this.statusSubject$.next(false);
      return;
    }

    this.statusSubject$.next(null);
  }

  private privateOnError(error: any) {
    this.statusSubject$.next(null);
  }

  private privateOnMessage(event: MessageEvent<any>): void {
    let map: any = JSON.parse(event.data);

    if (map['message_type'] === 'Connection') {
      this.authService.setCurrentUser({
        username: map['username'],
        email: map['email'],
        role: map['role'],
        image: map['image'],
      });
      this.statusSubject$.next(true);
    } else {
      this.messageSubject$.next(map);
    }
  }

  // Méthode pour écouter les messages de la socket
  public onMessage(): Observable<any> {
    return this.messageSubject$.asObservable();
  }

  // Méthode pour écouter les erreurs de la socket
  public onStatusChange(): Observable<any> {
    return this.statusSubject$.asObservable();
  }
}
