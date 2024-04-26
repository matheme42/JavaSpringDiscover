import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { Login } from '../../models/login.model';
import { tap } from 'rxjs';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.scss']
})
export class LoginComponent implements OnInit {


   login! : Login;

   errorMessage? : string;

  constructor(private auth: AuthService,
              private router: Router) { }

  ngOnInit(): void {
    this.login = new Login();
  }
 
  onLogin() : void {
    this.errorMessage = undefined;
    this.auth.login(this.login).pipe(
      tap(this.parseRegisterMessage.bind(this))
    ).subscribe();
    //this.router.navigateByUrl('/facesnaps');
  }

  parseRegisterMessage(map : any) : void {
    if (map['status']) {
      this.errorMessage = map['status'] == 401 ||400 ? "Username or Password invalid" : "An error occurd";
    } else {
      this.router.navigateByUrl('/')
    }
  }
}