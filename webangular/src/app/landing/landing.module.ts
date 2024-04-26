import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LandingPageComponent } from './components/landing-page/landing-page.component';
import { FormsModule } from '@angular/forms';
import { HomeComponent } from './components/home/home.component';
import { ProfilComponent } from './components/profil/profil.component';
import { NewGameComponent } from './components/new-game/new-game.component';



@NgModule({
  declarations: [
    LandingPageComponent,
    HomeComponent,
    ProfilComponent,
    NewGameComponent,
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
