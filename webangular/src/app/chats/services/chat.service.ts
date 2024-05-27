import { ChangeDetectorRef, Injectable, inject } from '@angular/core';
import { Observable, ReplaySubject, catchError, of, tap } from 'rxjs';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { User } from '../../auth/models/user.model';
import { Message } from '../../core/models/message.model';


export interface ChatMetaData {
  user : User,
  state : boolean,
}

export interface ChatData {
  user : User,
  message : Message[],
}

@Injectable({
  providedIn: 'root'
})
export class ChatService {

  private http: HttpClient = inject(HttpClient);

  public chatMessages = new Map<User, Message[]>();

  public chatOpenState = new Array<ChatMetaData>;

  reset() {
    this.chatMessages.clear();
    this.chatOpenState = [];
  }

  openChatFromUser(user: User): void {
    let exist: ChatMetaData | undefined = this.chatOpenState.find((e) => e.user === user)
    if (exist === undefined) {
      if (this.chatMessages.get(user) == undefined) {
        this.getChatMessageForUser(user).pipe(tap((m) => this.getChatMessageAnalyzeResponse(m, user))).subscribe();
      }
      this.chatOpenState.push({user : user, state : true})
    }
  }

  reduceChatForUser(data : ChatMetaData) : void {
    let index : number = this.chatOpenState.findIndex((d) => d === data);
    this.chatOpenState[index].state = false;
  }


  closeChatForUser(data : ChatMetaData) : void {
    let index : number = this.chatOpenState.findIndex((d) => d === data);
    this.chatOpenState.splice(index, 1);
  }

  getChatMessageForUser(user : User) {
    return this.http
    .get<Message[]>(`https://localhost/api/messages/${user.username}`)
    .pipe(
      catchError((error: HttpErrorResponse) =>
        of({ ...error.error, status: error.status })
      )
    );
  }

  getChatMessageAnalyzeResponse(data : any, user : User) : void {
    if (data['error']) return;
    this.chatMessages = this.chatMessages.set(user, data); // get the message of the user
  }

  
  sendChatMessageForUser(content : string, user : User) {
    return this.http
    .post<any>(`https://localhost/api/messages/${user.username}`, {
      "content" : content
    })
    .pipe(
      catchError((error: HttpErrorResponse) =>
        of({ ...error.error, status: error.status })
      )
    );
  }
}
