import { Component, OnDestroy, OnInit, inject } from '@angular/core';
import {
  InvitationRequest,
  PendingRequest,
  User,
} from '../../../auth/models/user.model';
import {
  FriendshipResponseInterface,
  FriendshipService,
} from '../../services/friendship.service';
import { Observable, Subscription, map, tap } from 'rxjs';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { SearchService } from '../../services/search.service';
import { AuthService } from '../../../core/services/auth.service';
import { SocketService } from '../../../core/services/socket.service';
import { SocketMessageType } from '../../../core/enum/socket_message.enum';
import { ChatService } from '../../../chats/services/chat.service';

@Component({
  selector: 'app-friendlist',
  templateUrl: './friendlist.component.html',
  styleUrl: './friendlist.component.scss',
})
export class FriendlistComponent implements OnInit, OnDestroy {
  constructor(
    private friendshipService: FriendshipService,
    private searchService: SearchService,
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private socketService: SocketService,
    private chatService: ChatService
  ) {}

  ngOnDestroy(): void {
    this.socketMessageSubscription?.unsubscribe();
  }

  private intervalId: any;

  amis!: User[];
  pendingAmis!: PendingRequest[];
  invitations!: InvitationRequest[];

  usernameForm!: FormGroup;
  usernamePreview$!: Observable<String>;

  searchedUser?: User[];

  inputFocus: boolean = false;

  socketMessageSubscription: Subscription | undefined;

  openChatFromUser(user: User): void {
    this.chatService.openChatFromUser(user);
  }

  ngOnInit(): void {
    this.friendshipService
      .friendship()
      .pipe(
        tap((res) => (this.amis = res.friends)),
        tap((res) => (this.pendingAmis = res.pendingInvitation)),
        tap((res) => (this.invitations = res.invitation))
      )
      .subscribe();

    this.usernameForm = this.formBuilder.group({
      name: [null, Validators.required],
    });
    this.usernamePreview$ = this.usernameForm.valueChanges.pipe(
      tap((data) => this.searchByUsername(data))
    );
    this.usernamePreview$.subscribe();

    this.socketMessageSubscription = this.socketService
      .onMessage()
      .pipe(tap((data) => this.onReceiveSocketMessage(data)))
      .subscribe();
  }

  onReceiveSocketMessage(data: any) {
    switch (data['message_type']) {
      case SocketMessageType.friendshipConnection: {
        let user: User | undefined = this.amis.find(
          (e) => e.username === data['username']
        );
        if (user == undefined) return;
        user.logged = true;
        break;
      }
      case SocketMessageType.friendshipDisconnection: {
        let user: User | undefined = this.amis.find(
          (e) => e.username === data['username']
        );
        if (user == undefined) return;
        user.logged = false;
        break;
      }
      case SocketMessageType.friendshipInvitation: {
        this.invitations.push({
          username: data['username'],
          role: data['role'],
          image: data['image'],
        });
        break;
      }
      case SocketMessageType.selfFriendshipInvitation: {
        this.pendingAmis.push({
          username: data['username'],
          role: data['role'],
          image: data['image'],
        });
        break;
      }
      case SocketMessageType.friendshipRemove: {
        this.analyseRemoveFriendShipResponse({}, data['username']);
        break;
      }
      case SocketMessageType.friendshipInvitationReply: {
        this.analyseRemoveFriendShipResponse({}, data['username']);
        this.amis.push({
          username: data['username'],
          email: data['email'],
          role: data['role'],
          image: data['image'],
          logged: data['logged'],
          last_connection: data['last_connection'],
        });
        break;
      }
    }
  }

  searchByUsername(data: any): void {
    clearInterval(this.intervalId);
    let username: string = data['name'];
    if (username == null) return;
    if (username.length < 1) {
      this.searchedUser = undefined;
      return;
    }
    this.intervalId = setInterval(() => {
      clearInterval(this.intervalId);
      this.searchService
        .search(username)
        .pipe(tap((data) => this.analysereplySearchByUsernameResponse(data)))
        .subscribe();
      this.intervalId = undefined;
    }, 500);
  }

  sendFriendShipInvitation(username: string): void {
    this.friendshipService.sendInvitation(username).subscribe();
  }

  replyFriendShip(username: string, response: boolean): void {
    this.friendshipService.replyInvitation(username, response).subscribe();
  }

  removeFriendShip(username: string): void {
    this.friendshipService.removeFriendShip(username).subscribe();
  }

  onFocus(event: any): void {
    this.inputFocus = event.type == 'focus' ? true : false;
    if (event.type == 'focus') {
      this.searchByUsername(this.usernameForm.value);
    }
  }

  analysereplySearchByUsernameResponse(datas: any): void {
    if (datas['error']) return;
    let users: User[] = [];

    (datas as User[]).forEach((elm) => {
      if (this.authService.getCurrentUser()?.username === elm.username) {
        return;
      }

      if (this.amis.findIndex((e) => e.username === elm.username) != -1) {
        return;
      }
      if (
        this.pendingAmis.findIndex((e) => e.username === elm.username) != -1
      ) {
        return;
      }
      if (
        this.invitations.findIndex((e) => e.username === elm.username) != -1
      ) {
        return;
      }
      users.push(elm);
    });

    this.searchedUser = users;
  }

  analysereplyFriendShipResponse(data: any, username: string): void {
    if (data['error']) return;
    let idx: number = this.invitations.findIndex(
      (u) => u.username === username
    );
    if (idx > -1 && idx < this.invitations.length) {
      this.invitations.splice(idx, 1);
    }
    if (!data['username']) return;
    this.amis.push(data);
  }

  analyseRemoveFriendShipResponse(data: any, username: string): void {
    if (data['error']) return;
    let idx: number = this.pendingAmis.findIndex(
      (u) => u.username === username
    );
    if (idx > -1 && idx < this.pendingAmis.length) {
      this.pendingAmis.splice(idx, 1);
      return;
    }

    idx = this.invitations.findIndex((u) => u.username === username);
    if (idx > -1 && idx < this.invitations.length) {
      this.invitations.splice(idx, 1);
      return;
    }

    idx = this.amis.findIndex((u) => u.username === username);
    if (idx > -1 && idx < this.amis.length) {
      this.amis.splice(idx, 1);
      return;
    }
  }
}
