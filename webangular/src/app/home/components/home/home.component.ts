import { Component, Inject, OnDestroy, OnInit } from '@angular/core';
import { SocketService } from '../../../core/services/socket.service';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss',
})
export class HomeComponent {}
