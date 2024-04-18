import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';

@Component({
  selector: 'app-landing-page',
  templateUrl: './landing-page.component.html',
  styleUrl: './landing-page.component.scss'
})
export class LandingPageComponent {

  userEmail! : string;

  constructor(private router: Router) { }

  onNavigate() : void {
    this.router.navigateByUrl("/facesnaps")
  }

  onSubmitForm(form: NgForm) : void {
    console.log(form.value);
  }
}
