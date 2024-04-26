import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../../core/services/auth.service';

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.scss'
})
export class LandingPageComponent {
  constructor(private router: Router, private auth : AuthService) { }

  onContinue() : void {
    this.router.navigateByUrl('/auth/login')
  }

  public get islogged() {
    return this.auth.getUserToken() != null
  }
}
