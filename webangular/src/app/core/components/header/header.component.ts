import { Component, OnInit } from '@angular/core';
import { AuthService } from '../../services/auth.service';
import { tap } from 'rxjs';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss'
})
export class HeaderComponent implements OnInit {

  constructor(private authService: AuthService, private router : Router) {}

  ngOnInit(): void {
    
  }



  logout() : void {
    this.authService.logout().pipe(tap(this.analyseLogoutResponse.bind(this))).subscribe();
  }


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
