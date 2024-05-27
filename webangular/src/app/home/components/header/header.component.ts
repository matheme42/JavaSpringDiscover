import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { tap } from 'rxjs';
import { Router } from '@angular/router';
import { ChatService } from '../../../chats/services/chat.service';
import { SocketService } from '../../../core/services/socket.service';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
})
export class HeaderComponent implements OnInit {
  menu: any = [
    {
      label: 'Bomberman',
      action: () => this.logout(),
    },
    {
      label: 'Profils',
      action: () => this.logout(),
    },
    {
      label: 'Nouvelle partie',
      action: () => this.logout(),
    },
    {
      label: 'Logout',
      action: () => this.logout(),
    },
  ];

  constructor(
    private authService: AuthService,
    private router: Router,
    private chatService: ChatService,
    private socketService : SocketService
  ) {}

  ngOnInit(): void {}

  logout(): void {
    this.authService
      .logout()
      .pipe(tap(this.analyseLogoutResponse.bind(this)))
      .subscribe();
  }

  analyseLogoutResponse(map: any): void {
    if (map == null) {
      this.authService.cleanUserToken();
      this.router.navigateByUrl('/login');
      this.chatService.reset();
      this.socketService.disconnect();
    }
  }
}
