import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { FormsModule } from '@angular/forms';



@NgModule({
  declarations: [
    LandingPageComponent,
  ],
  exports: [
    LandingPageComponent,
  ],
  imports: [
    CommonModule,
    FormsModule
  ]
})
export class LandingModule { }
