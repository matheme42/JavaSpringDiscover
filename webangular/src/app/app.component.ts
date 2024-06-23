import { Component, OnDestroy, OnInit } from '@angular/core';
import { SocketService } from './core/services/socket.service';
import { Subscription, tap } from 'rxjs';
import { Router } from '@angular/router';
import { AuthService } from './core/services/auth.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.scss',
})
export class AppComponent implements OnInit, OnDestroy {
  constructor(
    private socketService: SocketService,
    private router: Router,
    private authService: AuthService
  ) {}
  ngOnDestroy(): void {
    this.socketService.disconnect();
  }

  public socketStatus?: boolean;

  ngOnInit(): void {
    if (!this.authService.isLogged) {
      this.socketStatus = false;
      this.router.navigateByUrl('/login');
      return;
    }

    this.socketService
      .onStatusChange()
      .pipe(tap((data) => this.onReceiveStatus(data)).bind(this))
      .subscribe();
    this.socketService.connect();
  }

  onReceiveStatus(event: any) {
    if (event === null) {
      this.socketStatus = undefined;
      setTimeout(() => {
        this.socketService.connect();
      }, 3000);
    } else {
      this.socketStatus = event;
    }
  }
}
