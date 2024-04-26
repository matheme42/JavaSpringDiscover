import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../../core/services/auth.service';
import { Router } from '@angular/router';
import { tap } from 'rxjs';

@Component({
  selector: 'app-home',
  templateUrl: './home.component.html',
  styleUrl: './home.component.scss'
})
export class HomeComponent implements OnInit {

  constructor(private authService: AuthService, private router : Router) {}

  ngOnInit(): void {}

  logout() : void {
    this.authService.logout().pipe(tap(this.analyseLogoutResponse.bind(this))).subscribe();
  }

  public selectedpage : String = "home";

  isAuthenticate() : boolean {
    return this.authService.getUserToken() != null;
  }
  
  analyseLogoutResponse(map:any) : void {
    if (map == null) {
      this.authService.cleanUserToken();
      this.router.navigateByUrl('/auth/login');
    }
}
}
