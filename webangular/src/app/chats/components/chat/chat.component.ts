import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { ChatMetaData, ChatService } from '../../services/chat.service';
import { Observable, Subscription, tap } from 'rxjs';
import { User } from '../../../auth/models/user.model';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { AuthService } from '../../../core/services/auth.service';
import { SocketService } from '../../../core/services/socket.service';
import { SocketMessageType } from '../../../core/enum/socket_message.enum';
import { FriendshipService } from '../../../home/services/friendship.service';

@Component({
  selector: 'app-chat',
  templateUrl: './chat.component.html',
  styleUrl: './chat.component.scss',
})
export class ChatComponent implements OnInit, OnDestroy {
  messageForm!: FormGroup;

  faceSnapPreview$!: Observable<String>;

  socketMessageSubscription: Subscription | undefined;

  constructor(
    public chatService: ChatService,
    private formBuilder: FormBuilder,
    private authService: AuthService,
    private socketService: SocketService,
    private friendshipService: FriendshipService
  ) {}

  ngOnInit(): void {
    this.messageForm = this.formBuilder.group(
      {
        content: [null, Validators.required],
      },
      {
        updateOn: 'change',
      }
    );

    this.socketMessageSubscription = this.socketService
      .onMessage()
      .pipe(tap((data) => this.onReceiveSocketMessage(data)))
      .subscribe();
  }

  ngOnDestroy(): void {
    this.socketMessageSubscription?.unsubscribe();
  }

  onReceiveSocketMessage(data: any) {
    switch (data['message_type']) {
      case SocketMessageType.friendshipMessage: {
        let metaData: ChatMetaData | undefined =
          this.chatService.chatOpenState.find(
            (e) => e.user.username === data['user_name']
          );
        if (metaData === undefined) return;
        metaData.state = true;
        this.chatService.chatMessages.get(metaData.user)?.push({
          content: data['content'],
          user_image: data['user_image'],
          user_name: data['user_name'],
          date: data['date'],
        });
        break;
      }
    }
  }

  onSubmitMessage(user: User): void {
    this.chatService
      .sendChatMessageForUser(this.messageForm.value['content'], user)
      .pipe(tap((m) => this.analizeSendChatMessageResponse(m, user)))
      .subscribe();
  }

  analizeSendChatMessageResponse(data: any, user: User): void {
    if (data['error']) return;
    this.chatService.chatMessages.get(user)?.push({
      content: this.messageForm.value['content'],
      user_image: this.authService.getCurrentUser()!.image,
      user_name: this.authService.getCurrentUser()!.username,
      date: new Date(),
    });
    this.messageForm.reset();
  }

  reduceChat(data: ChatMetaData): void {
    this.chatService.reduceChatForUser(data);
  }

  closeChat(data: ChatMetaData): void {
    this.chatService.closeChatForUser(data);
  }

  public messages(user: User) {
    return this.chatService.chatMessages.get(user);
  }

  public isAuthUser(username: String): boolean {
    return username !== this.authService.getCurrentUser()?.username;
  }
}
